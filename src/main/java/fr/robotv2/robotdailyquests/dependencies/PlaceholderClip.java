package fr.robotv2.robotdailyquests.dependencies;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.quest.Quest;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderClip extends PlaceholderExpansion {

    private final RobotDailyQuest instance;
    public PlaceholderClip(RobotDailyQuest instance) {
        this.instance = instance;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "robotdailyquest";
    }

    @Override
    public @NotNull String getAuthor() {
        return this.instance.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return this.instance.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {

        final Player player;

        if(offlinePlayer == null || !offlinePlayer.isOnline() || (player = offlinePlayer.getPlayer()) == null) {
            return params;
        }

        final String[] args = params.split("_");

        ActiveQuest activeQuest;
        Quest quest;

        try {
            final QuestResetDelay delay = QuestResetDelay.valueOf(args[0]);
            final int number = Integer.parseInt(args[1]);
            activeQuest = this.instance.getQuestManager().getActiveQuests(delay).get(number);
        } catch (Exception e) {
            return params;
        }

        if(activeQuest == null || (quest = activeQuest.getQuest()) == null) {
            return params;
        }

        return switch (args[2].toLowerCase()) {
            case "name" -> quest.getName();
            case "reset" -> quest.getDelay().name().toLowerCase();
            case "difficulty" -> quest.getDifficulty().name().toLowerCase();
            case "required" -> String.valueOf(quest.getRequiredAmount());
            case "progress" -> String.valueOf(activeQuest.getCurrentProgress(player.getUniqueId()));
            default -> params;
        };
    }
}
