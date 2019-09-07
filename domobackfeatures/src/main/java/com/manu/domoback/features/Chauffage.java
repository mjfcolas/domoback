package com.manu.domoback.features;

import com.manu.domoback.arduinoreader.ExternalDataController;
import com.manu.domoback.arduinoreader.IExternalInfos;
import com.manu.domoback.common.UnsureBoolean;
import com.manu.domoback.features.api.features.IChauffage;
import com.manu.domoback.features.api.enums.INFOS;
import com.manu.domoback.features.chauffage.ChauffageInfo;
import com.manu.domoback.features.chauffage.IChauffageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Chauffage extends AbstractFeature implements IChauffage {

    private static final Logger LOGGER = LoggerFactory.getLogger(Chauffage.class.getName());

    private ExternalDataController arduinoReader;
    private final IChauffageInfo chauffageInfo = new ChauffageInfo();
    private int loopsSinceLastSync = 0;
    private static final int LOOPS_BEFORE_SYNC = 1000;
    private boolean syncRunning = false;

    public Chauffage() {
        super();
    }

    @Override
    public void init(final ExternalDataController arduinoReader) {
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
                if (this.loopsSinceLastSync > Chauffage.LOOPS_BEFORE_SYNC) {
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

            final Integer tempCommande = this.chauffageInfo.getChauffageTemp();

            LOGGER.trace("Recup température appartement");
            final Float curTemp = this.arduinoReader.getInfos().getTemperature();

            if (curTemp != null) {
                LOGGER.trace("Comparaison température: commande : {}, current: {}", tempCommande, curTemp);
                final boolean chauffageToLow = tempCommande >= curTemp;

                if (chauffageToLow) {
                    LOGGER.trace("Chauffage a allumer");
                    return new UnsureBoolean(true);
                } else {
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
        LOGGER.trace("Send command {} {} ", potentialCommand, chauffageState);
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
    public boolean save() {
        this.switchChauffage();
        this.fireDataChanged();
        return true;
    }

    @Override
    public void changeMode() {
        this.switchChauffageHourMode();
        this.fireDataChanged();
    }

    @Override
    public void changeTemperature(final boolean up) {
        try {
            int temp = this.jdbc.getCurrentTemp(false);
            temp += up ? 1 : -1;
            this.jdbc.setCurrentTemp(temp);
            this.fillInfos();
        } catch (final SQLException e) {
            LOGGER.error("Erreur de sauvegarde de la nouvelle température", e);
        }

    }

    @Override
    public void changeTemperatureHour(final boolean up, final Integer startHour) {
        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
            final Date startDate = dateFormat.parse(startHour.toString());
            int temp = this.jdbc.getTempForStartHour(startDate);
            temp += up ? 1 : -1;
            this.jdbc.setTemp(temp, startDate);
            this.fillInfos();
        } catch (final SQLException | ParseException e) {
            LOGGER.error("Erreur de sauvegarde de la nouvelle température", e);
        }

    }

    private void fillInfos() throws SQLException {
        LOGGER.trace("Chauffage.fillInfos");

        final Boolean newMode = this.jdbc.getCommandeChauffage();
        this.chauffageInfo.setChauffageMode(newMode);
        final Boolean newHourMode = this.jdbc.getHourModeChauffage();
        this.chauffageInfo.setChauffageHourMode(newHourMode);
        //Mode horaire: il faut récupérer une autre température de commande
        final boolean hourMode = this.chauffageInfo.getChauffageHourMode();
        LOGGER.trace("HourMode {}", hourMode);
        final Integer newTemp = this.jdbc.getCurrentTemp(hourMode);
        LOGGER.trace("newTemp {}", newTemp);
        this.chauffageInfo.setChauffageTemp(newTemp);
        this.chauffageInfo.setTempByHoursMap(this.jdbc.getTempMap());

        this.fireDataChanged();
    }

    private void switchChauffage() {
        try {
            this.chauffageInfo.setChauffageMode(this.jdbc.switchCommandeChauffage());
        } catch (final SQLException e) {
            LOGGER.error("Erreur de sauvegarde du switch chauffage", e);
        }
    }

    private void switchChauffageHourMode() {
        try {
            this.chauffageInfo.setChauffageHourMode(this.jdbc.switchHourModeChauffage());
        } catch (final SQLException e) {
            LOGGER.error("Erreur de sauvegarde du switch chauffage", e);
        }
    }

    public IChauffageInfo getChauffageInfo() {
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

        if (this.chauffageInfo.getChauffageHourMode() != null) {
            if (this.chauffageInfo.getChauffageHourMode()) {
                infos.put(INFOS.TEMPHOURMODE.name(), "ON");
            } else {
                infos.put(INFOS.TEMPHOURMODE.name(), "OFF");
            }

        } else {
            infos.put(INFOS.TEMPHOURMODE.name(), "N/A");
        }

        final DateFormat df = new SimpleDateFormat("HH");
        for (final Map.Entry<Date, Integer> progTemps : this.chauffageInfo.getTempByHoursMap().entrySet()) {
            final String hour = df.format(progTemps.getKey());
            infos.put(INFOS.TEMPCHAUFFTIME.name() + hour, progTemps.getValue().toString());
        }

        return infos;
    }
}
