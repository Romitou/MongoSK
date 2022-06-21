package fr.romitou.mongosk;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.logging.Logger;

public class LoggerHelper {

    private final static Logger LOGGER = Bukkit.getLogger();
    private final static Boolean DEBUG = MongoSK.getInstance().getConfig().getBoolean("debug-mode", false);
    private final static String PREFIX = "[MongoSK] ";

    public static void info(String... messages) {
        LOGGER.info(PREFIX + messages[0]);
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(LoggerHelper::addDebugInfo);
    }

    public static void debug(String... messages) {
        if (!DEBUG) return;
        LOGGER.config(PREFIX + messages[0]);
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(LoggerHelper::addDebugInfo);
    }

    public static void warn(String... messages) {
        LOGGER.warning(PREFIX + messages[0]);
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(LoggerHelper::addDebugInfo);
    }

    public static void severe(String... messages) {
        LOGGER.severe(PREFIX + messages[0]);
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(LoggerHelper::addDebugInfo);
    }

    private static void addDebugInfo(String message) {
        if (!DEBUG) return;
        LOGGER.config(PREFIX + "-> " + message);
    }

}
