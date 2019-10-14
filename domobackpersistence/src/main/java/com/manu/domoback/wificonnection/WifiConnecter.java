package com.manu.domoback.wificonnection;

import com.manu.domoback.conf.CONFKEYS;
import com.manu.domoback.conf.DomobackConf;
import com.manu.domoback.exceptions.BusinessRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WifiConnecter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WifiConnecter.class.getName());

    private WifiConnecter() {
        //Hide default constructor
    }

    public static boolean isAddressAvailable(String toTest) {
        boolean result = true;
        try {
            InetAddress.getByName(toTest);
        } catch (UnknownHostException e) {
            result = false;
            LOGGER.info("Connection lost", e);
        }
        return result;
    }

    public static void connect() throws BusinessRuntimeException {
        try {
            LOGGER.info("Connecting to wifi");
            String command = DomobackConf.get(CONFKEYS.WIFI_CONNECTER);
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.start();
        } catch (IOException e) {
            throw new BusinessRuntimeException("Could not connect to wifi");
        }
    }
}
