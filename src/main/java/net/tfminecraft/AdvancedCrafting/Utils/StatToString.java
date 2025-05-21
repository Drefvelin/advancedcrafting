package net.tfminecraft.AdvancedCrafting.Utils;

import java.util.HashMap;

import org.apache.commons.lang.WordUtils;

public class StatToString {
    public static HashMap<String, String> map = new HashMap<>();

    public static void add(String key, String value) {
        map.put(key, value);
    }

    public static String get(String key) {
        if(map.containsKey(key)) return map.get(key);
        return WordUtils.capitalize(key).replace("_", " ");
    }
}
