package fr.robotv2.robotdailyquests.quest;

import fr.robotv2.robotdailyquests.dependencies.VaultAPI;
import fr.robotv2.robotdailyquests.util.ColorUtil;
import fr.robotv2.robotdailyquests.util.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class QuestRewardProcessor {

    public void process(Player player, List<String> rewards) {

        for(String reward : rewards) {

            final String prefix = reward.split( " ")[0];

            reward = reward.substring(prefix.length() + 1).trim();
            reward = PlaceholderUtil.PLAYER_PLACEHOLDER.apply(player, reward);

            switch (prefix) {

                case "CLOSE" -> {
                    player.closeInventory();
                }

                case "[CONSOLE]" -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward);
                }

                case "[PLAYER]" -> {
                    Bukkit.dispatchCommand(player, reward);
                }

                case "[MONEY]" -> {
                    final double bal = Double.parseDouble(reward);
                    VaultAPI.giveMoney(player, bal);
                }

                case "[EXP_LEVEL]" -> {
                    final int level = Integer.parseInt(reward);
                    player.giveExpLevels(level);
                }

                case "[EXP_POINTS]" -> {
                    final int points = Integer.parseInt(reward);
                    player.giveExp(points);
                }

                case "[MESSAGE]" -> {
                    final String message = ColorUtil.color(reward);
                    player.sendMessage(message);
                }
            }
        }
    }

    public void process(Player player, Quest quest) {

        if(player == null || quest == null) {
            return;
        }

        final List<String> rewards = quest.getRewards().stream().map(string -> PlaceholderUtil.QUEST_PLACEHOLDER.apply(quest, string)).toList();
        this.process(player, rewards);
    }
}
