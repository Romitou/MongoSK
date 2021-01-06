package fr.romitou.mongosk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    private static final String PREFIX = "&7[&2Mongo&aSK&7] ";

    public static String getFormattedString(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static void consoleLog(String string) {
        Bukkit.getConsoleSender().sendMessage(getFormattedString(PREFIX + string));
    }

    public static <K, V> K getKeyByValue(HashMap<K, V> map, V value) {
        return map.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == value)
                .map(Map.Entry::getKey)
                .findAny()
                .orElse(null);
    }
}
