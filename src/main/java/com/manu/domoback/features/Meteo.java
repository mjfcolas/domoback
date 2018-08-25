package com.manu.domoback.features;

import com.manu.domoback.arduinoreader.IArduinoReader;
import com.manu.domoback.arduinoreader.IExternalInfos;
import com.manu.domoback.arduinoreader.INFOS;
import com.manu.domoback.common.CustLogger;
import com.manu.domoback.database.IJdbc;

import java.util.HashMap;
import java.util.Map;

public class Meteo extends AbstractFeature implements IMeteo {

    private IArduinoReader arduinoReader;
    private IExternalInfos meteoInfos;
    private String key;


    public Meteo(IArduinoReader arduinoReader, IJdbc jdbc, String key) {
        super(jdbc);
        this.name = key;
        this.key = key;
        this.arduinoReader = arduinoReader;
    }

    public void run() {
        if (arduinoReader.isReady()) {
            CustLogger.debprintln("Meteo thread running");
            //Information Arduino
            CustLogger.debprintln("METEO before writeData");
            arduinoReader.writeData(key);
            meteoInfos = arduinoReader.getInfos();
            this.fireDataChanged();
        }
    }

    @Override
    public IExternalInfos getRawInfos() {
        return meteoInfos;
    }

    @Override
    public Map<String, String> getInfos() {
        return formatInfos();
    }

    @Override
    public void save() {
        try {
            if (meteoInfos != null) {
                int type = 1;
                Float temperature = meteoInfos.getTemperature();
                if ("METEO2".equals(key)) {
                    type = 2;
                    temperature = meteoInfos.getTemperature2();
                }
                jdbc.saveMeteoInfos(temperature,
                        meteoInfos.getPressionRelative(),
                        meteoInfos.getPressionAbsolue(),
                        meteoInfos.getHygrometrie(),
                        type);
            }

        } catch (Exception e) {
            CustLogger.errprintln("Erreur de sauvegarde météo");
        }

    }

    private Map<String, String> formatInfos() {
        Map<String, String> infos = new HashMap<>();
        if (meteoInfos != null && meteoInfos.getTemperature() != null) {
            infos.put(INFOS.TEMP.name(), meteoInfos.getTemperature().toString());
        } else {
            infos.put(INFOS.TEMP.name(), "N/A");
        }

        if (meteoInfos != null && meteoInfos.getTemperature2() != null) {
            infos.put(INFOS.TEMP2.name(), meteoInfos.getTemperature2().toString());
        } else {
            infos.put(INFOS.TEMP2.name(), "N/A");
        }

        if (meteoInfos != null && meteoInfos.getPressionAbsolue() != null) {
            infos.put(INFOS.ABSPRE.name(), meteoInfos.getPressionAbsolue().toString());
        } else {
            infos.put(INFOS.ABSPRE.name(), "N/A");
        }

        if (meteoInfos != null && meteoInfos.getPressionRelative() != null) {
            infos.put(INFOS.RELPRE.name(), meteoInfos.getPressionRelative().toString());
        } else {
            infos.put(INFOS.RELPRE.name(), "N/A");
        }

        if (meteoInfos != null && meteoInfos.getHygrometrie() != null) {
            infos.put(INFOS.HYGROHUM.name(), meteoInfos.getHygrometrie().toString());
        } else {
            infos.put(INFOS.HYGROHUM.name(), "N/A");
        }

        return infos;
    }


}
