package fr.robotv2.robotdailyquests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class QuestSaveTask extends BukkitRunnable {

    private final RobotDailyQuest instance;
    public QuestSaveTask(RobotDailyQuest instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> this.instance.getDatabaseManager().savePlayerData(player.getUniqueId()));
    }
}
