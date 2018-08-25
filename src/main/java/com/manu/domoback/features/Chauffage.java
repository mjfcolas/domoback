package com.manu.domoback.features;

import com.manu.domoback.arduinoreader.IArduinoReader;
import com.manu.domoback.arduinoreader.IExternalInfos;
import com.manu.domoback.arduinoreader.INFOS;
import com.manu.domoback.chauffage.ChauffageInfo;
import com.manu.domoback.chauffage.IChauffageInfo;
import com.manu.domoback.common.CustLogger;
import com.manu.domoback.common.UnsureBoolean;
import com.manu.domoback.database.IJdbc;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Chauffage extends AbstractFeature implements IChauffage {

    private IArduinoReader arduinoReader;
    private IChauffageInfo chauffageInfo = new ChauffageInfo();
    private int loopsSinceLastSync = 0;
    private boolean syncRunning = false;

    public Chauffage(IArduinoReader arduinoReader, IJdbc jdbc) {
        super(jdbc);
        this.arduinoReader = arduinoReader;

    }

    @Override
    public void run() {
        CustLogger.traprintln("Chauffage.run");
        try {
            fillInfos();
            if (arduinoReader.isReady()) {
                CustLogger.traprintln("Chauffage.run - arduino ready ");
                //Lecture de l'état du chauffage d'après l'arduino si disponible
                IExternalInfos arduinoInfos = arduinoReader.getInfos();
                Boolean arduinChauffageState = arduinoInfos.getChauffageState();
                if (arduinChauffageState != null) {
                    CustLogger.debprintln("Synchro OK");
                    chauffageInfo.setChauffageState(arduinChauffageState);
                    chauffageInfo.setChauffageStateKnown(true);
                    syncRunning = false;
                }

                //Etat du chauffage connu, on peut gérer la commande
                if (chauffageInfo.getChauffageStateKnown()) {
                    CustLogger.traprintln("Potentielle commande chauffage");
                    this.sendCommand();
                } else if (!chauffageInfo.getChauffageStateKnown() && !syncRunning) {//Etat du chauffage inconnu, on synchronise
                    CustLogger.debprintln("Demande synchro");
                    syncRunning = true;
                    arduinoReader.writeData("CHINFO");
                }

                loopsSinceLastSync++;
                //Tous les 1000 tours de boucle, on considère qu'il faut resynchroniser l'état du chauffage
                if (loopsSinceLastSync > 1000) {
                    CustLogger.debprintln("Annulation synchro");
                    loopsSinceLastSync = 0;
                    syncRunning = false;
                    chauffageInfo.setChauffageStateKnown(false);
                }
            }
        } catch (SQLException e) {
            CustLogger.errprintln("Erreur de récupération des informations de chauffage");
        }
    }

    private UnsureBoolean getCommand() {
        //Le chauffage doit être éteint
        if (chauffageInfo.getChauffageState() && !chauffageInfo.getChauffageMode()) {
            CustLogger.traprintln("Chauffage eteint car mode OFF");
            return new UnsureBoolean(false);
        } else if (chauffageInfo.getChauffageMode() && arduinoReader.getInfos() != null) {//Le chauffage est en mode allumé, il faut voir s'il faut réguler la température
            CustLogger.traprintln("Recup température appartement");
            Float curTemp = arduinoReader.getInfos().getTemperature();

            if (curTemp != null) {
                CustLogger.traprintln("Comparaison température: commande : " + chauffageInfo.getChauffageTemp() + ", current: " + curTemp);
                boolean chauffageToLow = chauffageInfo.getChauffageTemp() >= curTemp;
                boolean chauffageToHigh = chauffageInfo.getChauffageTemp() < curTemp;

                if (chauffageToLow) {
                    CustLogger.traprintln("Chauffage a allumer");
                    return new UnsureBoolean(true);
                }
                if (chauffageToHigh) {
                    CustLogger.traprintln("Chauffage a eteindre");
                    return new UnsureBoolean(false);
                }
            }
        }
        return new UnsureBoolean(false, false);
    }

    private void sendCommand() {
        CustLogger.traprintln("Chauffage.sendCommand");
        UnsureBoolean potentialCommand = this.getCommand();
        Boolean chauffageState = chauffageInfo.getChauffageState();
        CustLogger.traprintln("Send command");
        if (potentialCommand.isSure() && chauffageState != null && chauffageState != potentialCommand.state()) {
            arduinoReader.writeData("CHAUFF " + (potentialCommand.state() ? "1" : "0"));
            chauffageInfo.setChauffageState(potentialCommand.state());
        }
    }

    @Override
    public Map<String, String> getInfos() {
        return formatInfos();
    }

    @Override
    public void save() {
        this.switchChauffage();
        this.fireDataChanged();
    }


    @Override
    public void changeTemperature(boolean up) {
        try {
            int temp = jdbc.getCurrentTemp();
            temp += up ? 1 : -1;
            jdbc.setCurrentTemp(temp);
            fillInfos();
        } catch (SQLException e) {
            CustLogger.errprintln("Erreur de sauvegarde de la nouvelle température");
        }

    }

    private void fillInfos() throws SQLException {
        CustLogger.traprintln("Chauffage.fillInfos");
        Boolean oldMode = chauffageInfo.getChauffageMode();
        Boolean newMode = jdbc.getCommandeChauffage();
        Integer oldTemp = chauffageInfo.getChauffageTemp();
        Integer newTemp = jdbc.getCurrentTemp();

        chauffageInfo.setChauffageMode(newMode);
        chauffageInfo.setChauffageTemp(newTemp);

        if (detectChange(oldMode, newMode, oldTemp, newTemp)) {
            this.fireDataChanged();
        }
    }

    protected boolean detectChange(Boolean oldMode, Boolean newMode, Integer oldTemp, Integer newTemp) {
        return oldTemp != newTemp || oldMode != newMode;
    }

    private void switchChauffage() {
        try {
            chauffageInfo.setChauffageMode(jdbc.switchCommandeChauffage());
        } catch (SQLException e) {
            CustLogger.errprintln("Erreur de sauvegarde du switch chauffage");
        }
    }

    public IChauffageInfo getChauffageInfo() {
        return this.chauffageInfo;
    }

    private Map<String, String> formatInfos() {
        Map<String, String> infos = new HashMap<>();
        if (chauffageInfo != null && chauffageInfo.getChauffageTemp() != null) {
            infos.put(INFOS.TEMPCHAUFF.name(), chauffageInfo.getChauffageTemp().toString());
        } else {
            infos.put(INFOS.TEMPCHAUFF.name(), "N/A");
        }

        if (chauffageInfo != null && chauffageInfo.getChauffageMode() != null) {
            if (chauffageInfo.getChauffageMode()) {
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
