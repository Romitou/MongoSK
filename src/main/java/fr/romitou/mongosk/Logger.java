package fr.romitou.mongosk;

import org.bukkit.ChatColor;

import java.util.Arrays;

public class Logger {

    private final static String PREFIX = "&7[&2Mongo&aSK&7] ";

    public static String getFormattedString(String message) {
        return ChatColor.translateAlternateColorCodes('&', PREFIX + message);
    }

    public static void info(String... message) {
        System.out.println(getFormattedString(ChatColor.WHITE + message[0]));
        if (message.length > 1)
            Arrays.stream(message).forEachOrdered(Logger::addDebugInfo);
    }

    public static void debug(String... message) {
        System.out.println(getFormattedString(ChatColor.GRAY + message[0]));
        if (message.length > 1)
            Arrays.stream(message).forEachOrdered(msg -> System.out.println(getFormattedString(ChatColor.GOLD + "→ " + msg)));
    }

    private static void addDebugInfo(String message) {
        System.out.println(getFormattedString(ChatColor.GOLD + "→ " + message));
    }

    public static void warn(String... message) {
        System.out.println(getFormattedString(ChatColor.GOLD + message[0]));
        if (message.length > 1)
            Arrays.stream(message).forEachOrdered(Logger::addDebugInfo);
    }

    public static void severe(String... message) {
        System.out.println(getFormattedString(ChatColor.RED + message[0]));
        if (message.length > 1)
            Arrays.stream(message).forEachOrdered(Logger::addDebugInfo);
    }

}
