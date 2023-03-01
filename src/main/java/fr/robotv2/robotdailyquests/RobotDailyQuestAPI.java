package fr.robotv2.robotdailyquests;

import fr.robotv2.robotdailyquests.data.impl.QuestPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class RobotDailyQuestAPI {

    private final static RobotDailyQuest INSTANCE = JavaPlugin.getPlugin(RobotDailyQuest.class);

    public static List<QuestPlayer.PlayerQuestArchive> getArchives(UUID player) {
        return QuestPlayer.getQuestPlayer(player).getArchives();
    }
}
