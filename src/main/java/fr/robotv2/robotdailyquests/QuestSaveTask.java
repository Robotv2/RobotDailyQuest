package fr.robotv2.robotdailyquests;

import fr.robotv2.robotdailyquests.data.QuestDatabaseManager;
import fr.robotv2.robotdailyquests.data.impl.QuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class QuestSaveTask extends BukkitRunnable {

    private final QuestDatabaseManager databaseManager;
    public QuestSaveTask(QuestDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream()
                .map(QuestPlayer::getQuestPlayer)
                .forEach(databaseManager::saveData);
    }
}
