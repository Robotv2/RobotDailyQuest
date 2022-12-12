package fr.robotv2.robotdailyquests;

import fr.robotv2.robotdailyquests.data.impl.LoadedQuest;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class QuestSaveTask extends BukkitRunnable {

    private final RobotDailyQuest instance;
    public QuestSaveTask(RobotDailyQuest instance) {
        this.instance = instance;
    }

    @Override
    public void run() {

        //Saving loaded quest.
        final List<LoadedQuest> quests = instance.getQuestManager().getLoadedQuests();
        quests.forEach(instance.getDatabaseManager()::saveLoadedQuest);

        //Saving player data.
        Bukkit.getOnlinePlayers().forEach(player -> instance.getDatabaseManager().savePlayerData(player.getUniqueId()));
    }
}
