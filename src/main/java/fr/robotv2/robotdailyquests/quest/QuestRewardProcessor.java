package fr.robotv2.robotdailyquests.quest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class QuestRewardProcessor {

    public void process(UUID playerUUID, Quest quest) {
        this.process(Bukkit.getPlayer(playerUUID), quest);
    }

    public void process(Player player, Quest quest) {

        if(player == null || quest == null) {
            return;
        }

        final List<String> rewards = quest.getRewards();

        for(String reward : rewards) {

            final String prefix = reward.split( " ")[0];

            reward = reward.substring(prefix.length() + 1);
            reward = reward.replace("%player%", player.getName());

            switch (prefix) {
                case "[CONSOLE]" -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward);
                case "[PLAYER]" -> Bukkit.dispatchCommand(player, reward);
            }
        }
    }
}
