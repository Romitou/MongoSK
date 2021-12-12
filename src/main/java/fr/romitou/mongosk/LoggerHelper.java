package fr.romitou.mongosk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.logging.Logger;

public class LoggerHelper {

    private final static Logger LOGGER = Bukkit.getLogger();
    private final static Boolean DEBUG = MongoSK.getInstance().getConfig().getBoolean("debug-mode", false);
    private final static String PREFIX = "&7[&2Mongo&aSK&7] ";

    public static String getFormattedString(String message) {
        return ChatColor.translateAlternateColorCodes('&', PREFIX + message);
    }

    public static void info(String... messages) {
        LOGGER.info(getFormattedString(ChatColor.WHITE + messages[0]));
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(LoggerHelper::addDebugInfo);
    }

    public static void debug(String... messages) {
        if (!DEBUG) return;
        LOGGER.config(getFormattedString(ChatColor.DARK_GRAY + messages[0]));
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(msg -> System.out.println(getFormattedString(ChatColor.DARK_GRAY + "-> " + msg)));
    }

    private static void addDebugInfo(String message) {
        if (!DEBUG) return;
        LOGGER.config(getFormattedString(ChatColor.DARK_GRAY + "-> " + message));
    }

    public static void warn(String... messages) {
        LOGGER.warning(getFormattedString(ChatColor.GOLD + messages[0]));
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(LoggerHelper::addDebugInfo);
    }

    public static void severe(String... messages) {
        LOGGER.severe(getFormattedString(ChatColor.RED + messages[0]));
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(LoggerHelper::addDebugInfo);
    }

}
