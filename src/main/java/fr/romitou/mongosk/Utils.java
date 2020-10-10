package fr.romitou.mongosk;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static <K, V> K getKeyByValue(HashMap<K, V> map, V value) {
        return map.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == value)
                .map(Map.Entry::getKey)
                .findAny()
                .orElse(null);
    }
}
