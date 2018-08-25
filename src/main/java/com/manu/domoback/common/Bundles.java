package com.manu.domoback.common;

import java.io.IOException;
import java.util.Properties;

public class Bundles {

    private static final Properties prop = new Properties();
    private static final Properties messages = new Properties();
    private static boolean initiatedProp = false;
    private static boolean initiatedMessage = false;

    private Bundles() {
        throw new IllegalStateException("Utility class");
    }

    public static Properties prop() {
        try {
            if (!initiatedProp) {
                prop.load(Bundles.class.getResourceAsStream("/config.properties"));
                initiatedProp = true;
            }
        } catch (IOException e) {
            CustLogger.errprintln("Fichier de config non trouvé");
            System.exit(-1);
        }
        return prop;
    }

    public static Properties messages() {
        try {
            if (!initiatedMessage) {
                messages.load(Bundles.class.getResourceAsStream("/messages.properties"));
                initiatedMessage = true;
            }
        } catch (IOException e) {
            CustLogger.errprintln("Fichier de messages non trouvé");
            System.exit(-1);
        }
        return messages;
    }
}
