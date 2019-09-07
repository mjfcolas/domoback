package com.manu.domoback;

import com.manu.domoback.arduinoreader.ArduinoReader;
import com.manu.domoback.arduinoreader.ExternalDataController;
import com.manu.domoback.cliinterface.display.WindowCliInterface;
import com.manu.domoback.conf.CONFKEYS;
import com.manu.domoback.conf.DomobackConf;
import com.manu.domoback.enums.FEATUREKEYS;
import com.manu.domoback.features.api.*;
import com.manu.domoback.features.api.factory.FeatureFactoryLoader;
import com.manu.domoback.features.api.factory.IChauffageFactory;
import com.manu.domoback.features.api.factory.IFeatureFactory;
import com.manu.domoback.features.api.factory.IMeteoFactory;
import com.manu.domoback.features.api.features.IChauffage;
import com.manu.domoback.features.api.features.IFeature;
import com.manu.domoback.features.api.features.IMeteo;
import com.manu.domoback.ui.UserInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class.getName());

    private static final IMeteoFactory meteoFactory = FeatureFactoryLoader.getFeatureFactory("MeteoFactory");
    private static final IChauffageFactory chauffageFactory = FeatureFactoryLoader.getFeatureFactory("ChauffageFactory");
    private static final IFeatureFactory teleinfoFactory = FeatureFactoryLoader.getFeatureFactory("TeleinfoFactory");

    private static final ExternalDataController arduinoReader = new ArduinoReader();
    private static final IMeteo meteo = meteoFactory.instantiate();
    private static final IMeteo meteo2 = meteoFactory.instantiate();
    private static final IChauffage chauffage = chauffageFactory.instantiate();
    private static final IFeature teleinfo = teleinfoFactory.instantiate();
    private static final IFeatureWrapper featureWrapper = new FeatureWrapper(meteo, meteo2, chauffage, teleinfo);

    private static final UserInterface cliInterface = new WindowCliInterface(featureWrapper, chauffage);

    App() {
        //Hide public constructor
    }

    public static void main() {

        LOGGER.info("Start Program");
        //Initialize features
        meteo.init(arduinoReader, FEATUREKEYS.METEO.name());
        meteo2.init(arduinoReader, FEATUREKEYS.METEO2.name());
        chauffage.init(arduinoReader);
        //Initialisation de la communication série
        arduinoReader.initialize();

        final boolean runMeteo1 = DomobackConf.get(CONFKEYS.MODE_METEO1_ACTIVATED).equals("1");
        final boolean runMeteo2 = DomobackConf.get(CONFKEYS.MODE_METEO2_ACTIVATED).equals("1");
        final boolean runTeleinfo = DomobackConf.get(CONFKEYS.MODE_TELEINFO_ACTIVATED).equals("1");
        final boolean runChauffage = DomobackConf.get(CONFKEYS.MODE_CHAUFFAGE_ACTIVATED).equals("1");

        //Temps entre les mises à jour et les sauvegardes
        final long askTimeMeteo = Long.parseLong(DomobackConf.get(CONFKEYS.METEO_ASKTIME));
        final long dbSaveTimeMeteo = Integer.parseInt(DomobackConf.get(CONFKEYS.METEO_DBSAVETIME));
        final long askTimeTeleinfo = Long.parseLong(DomobackConf.get(CONFKEYS.TELEINFO_ASKTIME));
        final long askTimeChauffage = Long.parseLong(DomobackConf.get(CONFKEYS.CHAUFFAGE_ASKTIME));

        if (runMeteo1) {
            final Timer meteoTimer = new Timer();
            meteoTimer.schedule(new FeatureRunner(meteo), 0, askTimeMeteo * 1000);
            final Timer meteoSaveTimer = new Timer();
            meteoSaveTimer.schedule(new FeatureSaver(meteo), 0, dbSaveTimeMeteo * 1000);
        }
        if (runMeteo2) {
            final Timer meteoTimer2 = new Timer();
            meteoTimer2.schedule(new FeatureRunner(meteo2), 0, askTimeMeteo * 1000);
            final Timer meteoSaveTimer2 = new Timer();
            meteoSaveTimer2.schedule(new FeatureSaver(meteo2), 0, dbSaveTimeMeteo * 1000);
        }
        if (runChauffage) {
            final Timer chauffageTimer = new Timer();
            chauffageTimer.schedule(new FeatureRunner(chauffage), 0, askTimeChauffage * 1000);
        }
        if (runTeleinfo) {
            final Timer teleinfoTimer = new Timer();
            teleinfoTimer.schedule(new FeatureRunner(teleinfo), 0, askTimeTeleinfo * 1000);
            final Timer teleinfoSaveTimer = new Timer();
            final long dbSaveTimeTeleinfo = Long.parseLong(DomobackConf.get(CONFKEYS.TELEINFO_DBSAVETIME));
            teleinfoSaveTimer.schedule(new FeatureSaver(teleinfo), 0, dbSaveTimeTeleinfo * 1000);
        }
        //Initialisation de l'interface
        cliInterface.displayInterface();
    }

    public static UserInterface getCliInterface() {
        return cliInterface;
    }
}
