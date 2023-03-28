package fr.robotv2.robotdailyquests.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public class ColorUtil {

    private ColorUtil() {}
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
