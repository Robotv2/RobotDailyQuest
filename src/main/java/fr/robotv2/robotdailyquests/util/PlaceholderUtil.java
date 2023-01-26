package fr.robotv2.robotdailyquests.util;

import fr.robotv2.robotdailyquests.quest.Quest;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

// Utility class.

public class PlaceholderUtil {

    private PlaceholderUtil() {}

    public static InternalPlaceholder<Quest> QUEST_PLACEHOLDER = (value, input) -> input
            .replace("%quest-name%", value.getName()
            .replace("%quest-next-reset%", DateUtil.getDateFormatted(value.getDelay().nextResetToEpochMilli())));

    public static InternalPlaceholder<Player> PLAYER_PLACEHOLDER = (value, input) -> input.replace("%player%", value.getName());

    public interface InternalPlaceholder<T> {
        String apply(T value, String input);
    }

    public static String parsePlaceholders(OfflinePlayer target, String input) {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") ? PlaceholderAPI.setPlaceholders(target, input) : input;
    }
}
