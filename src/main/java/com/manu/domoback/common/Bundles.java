package com.manu.domoback.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class Bundles {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bundles.class.getName());

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
            LOGGER.error("Fichier de config non trouvé");
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
            LOGGER.error("Fichier de messages non trouvé");
            System.exit(-1);
        }
        return messages;
    }
}
