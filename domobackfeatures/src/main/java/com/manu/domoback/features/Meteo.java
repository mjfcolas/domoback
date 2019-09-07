package com.manu.domoback.features;

import com.manu.domoback.arduinoreader.ExternalDataController;
import com.manu.domoback.arduinoreader.IExternalInfos;
import com.manu.domoback.features.api.features.IMeteo;
import com.manu.domoback.features.api.enums.INFOS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Meteo extends AbstractFeature implements IMeteo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Meteo.class.getName());

    private ExternalDataController arduinoReader;
    private IExternalInfos meteoInfos;
    private String key;

    public Meteo() {
        super();
    }

    @Override
    public void init(final ExternalDataController arduinoReader, final String key) {
        this.name = key;
        this.key = key;
        this.arduinoReader = arduinoReader;
    }

    @Override
    public void run() {
        if (this.arduinoReader.isReady()) {
            LOGGER.debug("Meteo thread running");
            //Information Arduino
            LOGGER.debug("METEO before writeData");
            this.arduinoReader.writeData(this.key);
            this.meteoInfos = this.arduinoReader.getInfos();
            this.fireDataChanged();
        }
    }

    @Override
    public Map<String, String> getInfos() {
        return this.formatInfos();
    }

    @Override
    public boolean save() {
        try {
            if (this.meteoInfos != null) {
                int type = 0;
                Float temperature = null;
                if ("METEO".equals(this.key)) {
                    type = 1;
                    temperature = this.meteoInfos.getTemperature();
                }
                if ("METEO2".equals(this.key)) {
                    type = 2;
                    temperature = this.meteoInfos.getTemperature2();
                }
                if ("METEO3".equals(this.key)) {
                    type = 3;
                    temperature = this.meteoInfos.getTemperature3();
                }
                this.jdbc.saveMeteoInfos(temperature,
                        this.meteoInfos.getPressionRelative(),
                        this.meteoInfos.getPressionAbsolue(),
                        this.meteoInfos.getHygrometrie(),
                        type);
            }

        } catch (final Exception e) {
            LOGGER.error("Erreur de sauvegarde météo", e);
        }
        return true;
    }

    private Map<String, String> formatInfos() {
        final Map<String, String> infos = new HashMap<>();
        if (this.meteoInfos != null && this.meteoInfos.getTemperature() != null) {
            infos.put(INFOS.TEMP.name(), this.meteoInfos.getTemperature().toString());
        }

        if (this.meteoInfos != null && this.meteoInfos.getTemperature2() != null) {
            infos.put(INFOS.TEMP2.name(), this.meteoInfos.getTemperature2().toString());
        }

        if (this.meteoInfos != null && this.meteoInfos.getPressionAbsolue() != null) {
            infos.put(INFOS.ABSPRE.name(), this.meteoInfos.getPressionAbsolue().toString());
        }

        if (this.meteoInfos != null && this.meteoInfos.getPressionRelative() != null) {
            infos.put(INFOS.RELPRE.name(), this.meteoInfos.getPressionRelative().toString());
        }

        if (this.meteoInfos != null && this.meteoInfos.getHygrometrie() != null) {
            infos.put(INFOS.HYGROHUM.name(), this.meteoInfos.getHygrometrie().toString());
        }

        return infos;
    }

}
