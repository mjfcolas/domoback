package com.manu.domoback;

import com.manu.domoback.arduinoreader.ArduinoReader;
import com.manu.domoback.arduinoreader.IArduinoReader;
import com.manu.domoback.cliinterface.WindowCliInterface;
import com.manu.domoback.common.Bundles;
import com.manu.domoback.common.DependanceFactory;
import com.manu.domoback.features.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class.getName());

    private static final IArduinoReader arduinoReader = new ArduinoReader();
    private static final IMeteo meteo = new Meteo(arduinoReader, DependanceFactory.getJdbc(), "METEO");
    private static final IMeteo meteo2 = new Meteo(arduinoReader, DependanceFactory.getJdbc(), "METEO2");
    private static final IChauffage chauffage = new Chauffage(arduinoReader, DependanceFactory.getJdbc());
    private static final IFeature teleinfo = new Teleinfo(DependanceFactory.getJdbc());
    private static final IFeatureWrapper featureWrapper = new FeatureWrapper(meteo, chauffage, teleinfo);
    private static final WindowCliInterface cliInterface = new WindowCliInterface(featureWrapper, chauffage);

    public static void main(String[] args) {

        LOGGER.info("Start Program");
        //Initialisation de la communication série
        arduinoReader.initialize();

        //Timers pour gérer la météo
        long askTimeMeteo = Long.parseLong(Bundles.prop().getProperty("meteo.asktime"));

        Timer meteoTimer = new Timer();
        meteoTimer.schedule(new FeatureRunner(meteo), 0, askTimeMeteo * 1000);
        Timer meteoTimer2 = new Timer();
        meteoTimer2.schedule(new FeatureRunner(meteo2), 0, askTimeMeteo * 1000);


        long dbSaveTimeMeteo = Integer.parseInt(Bundles.prop().getProperty("meteo.dbsavetime"));
        Timer meteoSaveTimer = new Timer();
        Timer meteoSaveTimer2 = new Timer();
        meteoSaveTimer.schedule(new FeatureSaver(meteo), 0, dbSaveTimeMeteo * 1000);
        meteoSaveTimer2.schedule(new FeatureSaver(meteo2), 0, dbSaveTimeMeteo * 1000);

        //Timer pour gérer le chauffage
        Timer chauffageTimer = new Timer();
        long askTimeChauffage = Long.parseLong(Bundles.prop().getProperty("chauffage.asktime"));
        chauffageTimer.schedule(new FeatureRunner(chauffage), 0, askTimeChauffage * 1000);

        //Timers pour gérer la téléinfo
        Timer teleinfoTimer = new Timer();
        long askTimeTeleinfo = Long.parseLong(Bundles.prop().getProperty("teleinfo.asktime"));
        teleinfoTimer.schedule(new FeatureRunner(teleinfo), 0, askTimeTeleinfo * 1000);

        Timer teleinfoSaveTimer = new Timer();
        long dbSaveTimeTeleinfo = Long.parseLong(Bundles.prop().getProperty("teleinfo.dbsavetime"));
        teleinfoSaveTimer.schedule(new FeatureSaver(teleinfo), 0, dbSaveTimeTeleinfo * 1000);

        //Initialisation de l'interface
        cliInterface.displayInterface();
        System.exit(0);
    }
}
