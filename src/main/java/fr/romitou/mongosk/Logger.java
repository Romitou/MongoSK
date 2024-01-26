package fr.romitou.mongosk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;

public class Logger {
    private static final String INFO_PREFIX = "&7[&aMongo&2SK&7] &f";
    private static final String DEBUG_PREFIX = "&7[&aMongo&2SK&7] ";
    private static final String SEVERE_PREFIX = "&7[&aMongo&2SK&7] &c";

    public static String getColoredString(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static void info(String... messages) {
        Bukkit.getConsoleSender().sendMessage(getColoredString(INFO_PREFIX) + messages[0]);
        if (messages.length > 1)
            Arrays.stream(messages)
                    .skip(1)
                    .forEachOrdered(Logger::debug);
    }

    public static void debug(String message) {
        Bukkit.getConsoleSender().sendMessage(getColoredString(DEBUG_PREFIX + message));
    }

    public static void severe(String... messages) {
        Bukkit.getConsoleSender().sendMessage(getColoredString(SEVERE_PREFIX) + messages[0]);
        if (messages.length > 1)
            Arrays.stream(messages)
                    .skip(1)
                    .forEachOrdered(Logger::debug);
    }
}
