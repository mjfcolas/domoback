package com.manu.domoback.conf;

public enum CONFKEYS {
    TELEINFO_FILETOUSE("teleinfo.filetouse"),
    TELEINFO_PROCESSRECORD("teleinfo.processrecord"),
    TELEINFO_DBSAVETIME("teleinfo.dbsavetime"),
    TELEINFO_TRAMETIME("teleinfo.trametime"),
    TELEINFO_ASKTIME("teleinfo.asktime"),

    TELEINFO_GAIN("teleinfo.gain"),
    TELEINFO_OFFSET("teleinfo.offset"),
    TELEINFO_REDRESSER("teleinfo.redresser"),
    TELEINFO_INVERSE("teleinfo.inverse"),
    TELEINFO_MIXER("teleinfo.mixer"),

    METEO_DBSAVETIME("meteo.dbsavetime"),
    METEO_ASKTIME("meteo.asktime"),

    CHAUFFAGE_ASKTIME("chauffage.asktime"),

    JDBC_ACTIVATED("jdbc.activated"),
    JDBC_DRIVER("jdbc.driver"),
    JDBC_URL("jdbc.url"),
    JDBC_TEST_URL("jdbc.test.url"),
    JDBC_USERNAME("jdbc.username"),
    JDBC_PASSWORD("jdbc.password"),

    WIFI_CONNECTER("wifi.connecter.path"),

    MODE_METEO1_ACTIVATED("mode.meteo1.activated"),
    MODE_METEO2_ACTIVATED("mode.meteo2.activated"),
    MODE_TELEINFO_ACTIVATED("mode.teleinfo.activated"),
    MODE_CHAUFFAGE_ACTIVATED("mode.chauffage.activated"),

    SERIAL_DATA_RATE("serial.data.rate"),

    LOG_ERROR_GENERIC("An error occured");

    private final String propKey;

    CONFKEYS(String propKey){
        this.propKey = propKey;
    }

    public String getPropKey(){
        return this.propKey;
    }
}
