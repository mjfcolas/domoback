package com.manu.domoback.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class DomobackConf {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomobackConf.class.getName());

    private static final Properties conf = new Properties();
    private static boolean initiatedProp = false;

    private DomobackConf() {
        throw new IllegalStateException("Utility class");
    }

    private static Properties conf() {
        try {
            if (!initiatedProp) {
                conf.load(DomobackConf.class.getResourceAsStream("/config.properties"));
                initiatedProp = true;
            }
        } catch (IOException e) {
            LOGGER.error("Fichier de config non trouvé");
            System.exit(-1);
        }
        return conf;
    }

    public static String get(CONFKEYS key){
        return DomobackConf.conf().getProperty(key.getPropKey());
    }
}
