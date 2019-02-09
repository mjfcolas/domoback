package com.manu.domoback;

import com.manu.domoback.arduinoreader.ArduinoReader;
import com.manu.domoback.arduinoreader.IArduinoReader;
import com.manu.domoback.cliinterface.IUserInterface;
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
    private static final IChauffage chauffage = new Chauffage(arduinoReader, DependanceFactory.getJdbc(), 1000);
    private static final IFeature teleinfo = new Teleinfo(DependanceFactory.getJdbc());
    private static final IFeatureWrapper featureWrapper = new FeatureWrapper(meteo, chauffage, teleinfo);

    private static final IUserInterface cliInterface = new WindowCliInterface(featureWrapper, chauffage);

    App() {
        //Hide public constructor
    }

    public static void main() {

        LOGGER.info("Start Program");
        //Initialisation de la communication série
        arduinoReader.initialize();

        final boolean runMeteo1 = Bundles.prop().getProperty("mode.meteo1.activated").equals("1");
        final boolean runMeteo2 = Bundles.prop().getProperty("mode.meteo2.activated").equals("1");
        final boolean runTeleinfo = Bundles.prop().getProperty("mode.teleinfo.activated").equals("1");
        final boolean runChauffage = Bundles.prop().getProperty("mode.chauffage.activated").equals("1");

        //Temps entre les mises à jour et les sauvegardes
        final long askTimeMeteo = Long.parseLong(Bundles.prop().getProperty("meteo.asktime"));
        final long dbSaveTimeMeteo = Integer.parseInt(Bundles.prop().getProperty("meteo.dbsavetime"));
        final long askTimeTeleinfo = Long.parseLong(Bundles.prop().getProperty("teleinfo.asktime"));
        final long askTimeChauffage = Long.parseLong(Bundles.prop().getProperty("chauffage.asktime"));

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
            final long dbSaveTimeTeleinfo = Long.parseLong(Bundles.prop().getProperty("teleinfo.dbsavetime"));
            teleinfoSaveTimer.schedule(new FeatureSaver(teleinfo), 0, dbSaveTimeTeleinfo * 1000);
        }
        //Initialisation de l'interface
        cliInterface.displayInterface();
    }

    static IUserInterface getCliInterface() {
        return cliInterface;
    }
}
