package fr.robotv2.robotdailyquests;

import com.j256.ormlite.stmt.DeleteBuilder;
import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.data.impl.QuestPlayer;
import fr.robotv2.robotdailyquests.enums.QuestDifficulty;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.events.DelayQuestResetEvent;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QuestResetService {

    private final RobotDailyQuest instance;
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(3);

    public QuestResetService(RobotDailyQuest instance) {
        this.instance = instance;
        EnumSet.allOf(QuestResetDelay.class).forEach(this::scheduleNextReset);
    }

    public void scheduleNextReset(QuestResetDelay delay) {
        final long period = delay.nextResetToEpochMilli() - System.currentTimeMillis();
        service.schedule(this.getResetRunnable(delay, true), period, TimeUnit.MILLISECONDS);
    }

    public Runnable getResetRunnable(QuestResetDelay delay, boolean reschedule) {
        return () -> {

            try {
                final DeleteBuilder<ActiveQuest, Integer> builder = instance.getDatabaseManager().getActiveQuestData().getDao().deleteBuilder();
                builder.setWhere(builder.where().eq("quest-delay-type", delay));
                builder.delete();
            } catch (SQLException e) {
                instance.getLogger().warning("An error occurred while deleting loaded quests: " + delay);
                e.printStackTrace();
                return;
            }

            for(QuestPlayer questPlayer : QuestPlayer.getRegistered()) {
                this.reset(questPlayer.getUniqueId(), delay, false);
            }

            instance.getSaveTask().run();

            if(reschedule) {
                this.scheduleNextReset(delay);
            }

            Bukkit.getPluginManager().callEvent(new DelayQuestResetEvent(delay));
        };
    }

    public void reset(UUID player, QuestResetDelay delay) {
        this.reset(player, delay, true);
    }

    public void reset(UUID player, QuestResetDelay delay, boolean removeFromDatabase) {

        if(removeFromDatabase) {
            this.removeQuestFromDatabase(player, delay);
        }

        final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(player);

        if(questPlayer != null) {

            questPlayer.removeActiveQuest(delay);

            for(QuestDifficulty difficulty : QuestDifficulty.VALUES) {
                final int max = delay.getMax(difficulty);
                this.instance.getQuestManager().fillQuest(questPlayer, delay, difficulty, max);
            }
        }

        this.instance.debug("The quests of a player (%s) has been reset.", player.toString());
    }

    private void removeQuestFromDatabase(UUID playerUUID, QuestResetDelay delay) {
        try {
            final DeleteBuilder<ActiveQuest, Integer> builder = instance.getDatabaseManager().getActiveQuestData().getDao().deleteBuilder();
            builder.setWhere(builder.where().eq("quest-delay-type", delay).and().eq("owner", playerUUID));
            builder.delete();
        } catch (SQLException e) {
            instance.getLogger().warning(String.format("An error occurred while deleting loaded quests %s for player %s", delay.name(), playerUUID.toString()));
            e.printStackTrace();
        }
    }
}
