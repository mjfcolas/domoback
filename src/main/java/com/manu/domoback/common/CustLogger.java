package com.manu.domoback.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CustLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustLogger.class.getName());

    private CustLogger() {
        throw new IllegalStateException("Utility class");
    }

    public static void println(String message, String prefix, String mode) {

        if (prefix.isEmpty()) {
            prefix += "   ";
        }

        String template = "{} {}";
        Object[] options = new Object[]{prefix, message};

        if ("OUT".equals(mode)) {
            LOGGER.info(template, options);
        } else if ("ERR".equals(mode)) {
            LOGGER.error(template, options);
        } else if ("DEB".equals(mode)) {
            LOGGER.debug(template, options);
        } else if ("TRA".equals(mode)) {
            LOGGER.trace(template, options);
        }
    }

    public static void outprintln(String message, String prefix) {
        println(message, prefix, "OUT");
    }

    public static void outprintln(String message) {
        outprintln(message, "");
    }

    public static void errprintln(String message, String prefix) {
        println(message, prefix, "ERR");
    }

    public static void errprintln(String message) {
        errprintln(message, "");
    }

    public static void debprintln(String message, String prefix) {
        println(message, prefix, "DEB");
    }

    public static void debprintln(String message) {
        debprintln(message, "");
    }

    public static void logException(Exception e) {
        StringWriter stacktrace = new StringWriter();
        PrintWriter pwri = new PrintWriter(stacktrace);
        e.printStackTrace(pwri);
        errprintln(stacktrace.toString());
    }

    public static void traprintln(String message, String prefix) {
        println(message, prefix, "TRA");
    }

    public static void traprintln(String message) {
        traprintln(message, "");
    }
}
