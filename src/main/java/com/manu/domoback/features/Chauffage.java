package com.manu.domoback.features;

import com.manu.domoback.arduinoreader.IArduinoReader;
import com.manu.domoback.arduinoreader.IExternalInfos;
import com.manu.domoback.arduinoreader.INFOS;
import com.manu.domoback.chauffage.ChauffageInfo;
import com.manu.domoback.chauffage.IChauffageInfo;
import com.manu.domoback.common.UnsureBoolean;
import com.manu.domoback.database.IJdbc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Chauffage extends AbstractFeature implements IChauffage {

    private static final Logger LOGGER = LoggerFactory.getLogger(Chauffage.class.getName());

    private final IArduinoReader arduinoReader;
    private final IChauffageInfo chauffageInfo = new ChauffageInfo();
    private int loopsSinceLastSync = 0;
    private boolean syncRunning = false;

    public Chauffage(final IArduinoReader arduinoReader, final IJdbc jdbc) {
        super(jdbc);
        this.arduinoReader = arduinoReader;

    }

    @Override
    public void run() {
        LOGGER.trace("Chauffage.run");
        try {
            this.fillInfos();
            if (this.arduinoReader.isReady()) {
                LOGGER.trace("Chauffage.run - arduino ready ");
                //Lecture de l'état du chauffage d'après l'arduino si disponible
                final IExternalInfos arduinoInfos = this.arduinoReader.getInfos();
                final Boolean arduinChauffageState = arduinoInfos.getChauffageState();
                if (arduinChauffageState != null) {
                    LOGGER.debug("Synchro OK");
                    this.chauffageInfo.setChauffageState(arduinChauffageState);
                    this.chauffageInfo.setChauffageStateKnown(true);
                    this.syncRunning = false;
                }

                //Etat du chauffage connu, on peut gérer la commande
                if (this.chauffageInfo.getChauffageStateKnown()) {
                    LOGGER.trace("Potentielle commande chauffage");
                    this.sendCommand();
                } else if (!this.chauffageInfo.getChauffageStateKnown() && !this.syncRunning) {//Etat du chauffage inconnu, on synchronise
                    LOGGER.debug("Demande synchro");
                    this.syncRunning = true;
                    this.arduinoReader.writeData("CHINFO");
                }

                this.loopsSinceLastSync++;
                //Tous les 1000 tours de boucle, on considère qu'il faut resynchroniser l'état du chauffage
                if (this.loopsSinceLastSync > 1000) {
                    LOGGER.debug("Annulation synchro");
                    this.loopsSinceLastSync = 0;
                    this.syncRunning = false;
                    this.chauffageInfo.setChauffageStateKnown(false);
                }
            }
        } catch (final SQLException e) {
            LOGGER.error("Erreur de récupération des informations de chauffage", e);
        }
    }

    private UnsureBoolean getCommand() {
        //Le chauffage doit être éteint
        if (this.chauffageInfo.getChauffageState() && !this.chauffageInfo.getChauffageMode()) {
            LOGGER.trace("Chauffage eteint car mode OFF");
            return new UnsureBoolean(false);
        } else if (this.chauffageInfo.getChauffageMode() && this.arduinoReader.getInfos() != null) {//Le chauffage est en mode allumé, il faut voir s'il faut réguler la température
            LOGGER.trace("Recup température appartement");
            final Float curTemp = this.arduinoReader.getInfos().getTemperature();

            if (curTemp != null) {
                LOGGER.trace("Comparaison température: commande : {}, current: {}", this.chauffageInfo.getChauffageTemp(), curTemp);
                final boolean chauffageToLow = this.chauffageInfo.getChauffageTemp() >= curTemp;
                final boolean chauffageToHigh = this.chauffageInfo.getChauffageTemp() < curTemp;

                if (chauffageToLow) {
                    LOGGER.trace("Chauffage a allumer");
                    return new UnsureBoolean(true);
                }
                if (chauffageToHigh) {
                    LOGGER.trace("Chauffage a eteindre");
                    return new UnsureBoolean(false);
                }
            }
        }
        return new UnsureBoolean(false, false);
    }

    private void sendCommand() {
        LOGGER.trace("Chauffage.sendCommand");
        final UnsureBoolean potentialCommand = this.getCommand();
        final Boolean chauffageState = this.chauffageInfo.getChauffageState();
        LOGGER.trace("Send command");
        if (potentialCommand.isSure() && chauffageState != null && chauffageState != potentialCommand.state()) {
            this.arduinoReader.writeData("CHAUFF " + (potentialCommand.state() ? "1" : "0"));
            this.chauffageInfo.setChauffageState(potentialCommand.state());
        }
    }

    @Override
    public Map<String, String> getInfos() {
        return this.formatInfos();
    }

    @Override
    public void save() {
        this.switchChauffage();
        this.fireDataChanged();
    }

    @Override
    public void changeTemperature(final boolean up) {
        try {
            int temp = this.jdbc.getCurrentTemp();
            temp += up ? 1 : -1;
            this.jdbc.setCurrentTemp(temp);
            this.fillInfos();
        } catch (final SQLException e) {
            LOGGER.error("Erreur de sauvegarde de la nouvelle température", e);
        }

    }

    private void fillInfos() throws SQLException {
        LOGGER.trace("Chauffage.fillInfos");
        final Boolean oldMode = this.chauffageInfo.getChauffageMode();
        final Boolean newMode = this.jdbc.getCommandeChauffage();
        final Integer oldTemp = this.chauffageInfo.getChauffageTemp();
        final Integer newTemp = this.jdbc.getCurrentTemp();

        this.chauffageInfo.setChauffageMode(newMode);
        this.chauffageInfo.setChauffageTemp(newTemp);

        if (this.detectChange(oldMode, newMode, oldTemp, newTemp)) {
            this.fireDataChanged();
        }
    }

    private boolean detectChange(final Boolean oldMode, final Boolean newMode, final Integer oldTemp, final Integer newTemp) {
        return oldTemp != null && oldMode != null && (!oldTemp.equals(newTemp) || !oldMode.equals(newMode));
    }

    private void switchChauffage() {
        try {
            this.chauffageInfo.setChauffageMode(this.jdbc.switchCommandeChauffage());
        } catch (final SQLException e) {
            LOGGER.error("Erreur de sauvegarde du switch chauffage", e);
        }
    }

    IChauffageInfo getChauffageInfo() {
        return this.chauffageInfo;
    }

    private Map<String, String> formatInfos() {
        final Map<String, String> infos = new HashMap<>();
        if (this.chauffageInfo.getChauffageTemp() != null) {
            infos.put(INFOS.TEMPCHAUFF.name(), this.chauffageInfo.getChauffageTemp().toString());
        } else {
            infos.put(INFOS.TEMPCHAUFF.name(), "N/A");
        }

        if (this.chauffageInfo.getChauffageMode() != null) {
            if (this.chauffageInfo.getChauffageMode()) {
                infos.put(INFOS.MODECHAUFF.name(), "ON");
            } else {
                infos.put(INFOS.MODECHAUFF.name(), "OFF");
            }

        } else {
            infos.put(INFOS.MODECHAUFF.name(), "N/A");
        }

        return infos;
    }
}
