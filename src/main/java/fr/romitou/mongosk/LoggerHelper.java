package fr.romitou.mongosk;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerHelper {

    private static final Logger LOGGER;
    private static final boolean DEBUG;
    private final static String PREFIX = "[MongoSK] ";

    static {
        Logger logger = null;
        try {
            logger = Bukkit.getLogger();
        } catch (Throwable t) {
            // Bukkit not available or failed to load
            logger = Logger.getLogger("MongoSK");
            logger.log(Level.FINE, "Bukkit logger not available, falling back to default logger", t);
        }
        if (logger == null) {
            logger = Logger.getLogger("MongoSK");
        }
        LOGGER = logger;

        boolean debug = false;
        try {
            debug = MongoSK.getInstance().getConfig().getBoolean("debug-mode", false);
        } catch (Throwable t) {
            // MongoSK not available or failed to load
            LOGGER.log(Level.FINE, "MongoSK instance not available or failed to load config, debug mode disabled", t);
        }
        DEBUG = debug;
    }

    public static void info(String... messages) {
        LOGGER.info(messages[0]);
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(LoggerHelper::addDebugInfo);
    }

    public static void debug(String... messages) {
        if (!DEBUG) return;
        LOGGER.config(messages[0]);
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(LoggerHelper::addDebugInfo);
    }

    public static void warn(String... messages) {
        LOGGER.warning(messages[0]);
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(LoggerHelper::addDebugInfo);
    }

    public static void severe(String... messages) {
        LOGGER.severe(messages[0]);
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(LoggerHelper::addDebugInfo);
    }

    public static void severe(String message, Throwable e) {
        LOGGER.log(java.util.logging.Level.SEVERE, message, e);
    }

    private static void addDebugInfo(String message) {
        if (!DEBUG) return;
        LOGGER.config("-> " + message);
    }

}
