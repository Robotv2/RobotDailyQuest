package fr.robotv2.robotdailyquests.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlaceholderUtil {
    public static String parsePlaceholders(OfflinePlayer target, String input) {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") ? PlaceholderAPI.setPlaceholders(target, input) : input;
    }
}
