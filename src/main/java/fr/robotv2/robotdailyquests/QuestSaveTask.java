package fr.robotv2.robotdailyquests;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class QuestSaveTask extends BukkitRunnable {

    private final RobotDailyQuest instance;
    public QuestSaveTask(RobotDailyQuest instance) {
        this.instance = instance;
    }

    @Override
    public void run() {

        //Saving loaded quest.
        instance.getQuestManager().getActiveQuests()
                .forEach(instance.getDatabaseManager()::saveLoadedQuest);

        //Saving player data.
        Bukkit.getOnlinePlayers()
                .forEach(player -> instance.getDatabaseManager().savePlayerData(player.getUniqueId()));
    }
}
