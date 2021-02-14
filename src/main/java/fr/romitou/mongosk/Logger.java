package fr.romitou.mongosk;

import org.bukkit.ChatColor;

import java.util.Arrays;

public class Logger {

    private final static String PREFIX = "&7[&2Mongo&aSK&7] ";
    private final static Boolean DEBUG = MongoSK.getConfiguration().getBoolean("debug-mode", false);

    public static String getFormattedString(String message) {
        return ChatColor.translateAlternateColorCodes('&', PREFIX + message);
    }

    public static void info(String... messages) {
        System.out.println(getFormattedString(ChatColor.WHITE + messages[0]));
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(Logger::addDebugInfo);
    }

    public static void debug(String... messages) {
        if (!DEBUG) return;
        System.out.println(getFormattedString(ChatColor.DARK_GRAY + messages[0]));
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(msg -> System.out.println(getFormattedString(ChatColor.DARK_GRAY + "-> " + msg)));
    }

    private static void addDebugInfo(String message) {
        if (!DEBUG) return;
        System.out.println(getFormattedString(ChatColor.DARK_GRAY + "-> " + message));
    }

    public static void warn(String... messages) {
        System.out.println(getFormattedString(ChatColor.GOLD + messages[0]));
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(Logger::addDebugInfo);
    }

    public static void severe(String... messages) {
        System.out.println(getFormattedString(ChatColor.RED + messages[0]));
        if (messages.length > 1)
            Arrays.stream(messages)
                .skip(1)
                .forEachOrdered(Logger::addDebugInfo);
    }

}
