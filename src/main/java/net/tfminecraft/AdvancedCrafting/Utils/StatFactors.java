package net.tfminecraft.AdvancedCrafting.Utils;

import java.util.HashMap;

public class StatFactors {
    public static HashMap<String, Double> map = new HashMap<>();
    

    public static void add(String s, Double i) {
        map.put(s, i);
    }

    public static boolean has(String s) {
        return map.containsKey(s);
    }

    public static Double get(String s) {
        return map.get(s);
    }
}
