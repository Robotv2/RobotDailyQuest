package fr.robotv2.robotdailyquests;

import com.j256.ormlite.stmt.DeleteBuilder;
import fr.robotv2.robotdailyquests.data.impl.LoadedQuest;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;

import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QuestResetService {

    private final RobotDailyQuest instance;
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(3);

    public QuestResetService(RobotDailyQuest instance) {
        this.instance = instance;

    }

    public void scheduleNextReset(QuestResetDelay delay) {
        final long period = delay.nextReset().toInstant(ZoneOffset.UTC).toEpochMilli() - System.currentTimeMillis();
        service.schedule(this.getResetRunnable(delay, true), period, TimeUnit.MILLISECONDS);
    }

    public Runnable getResetRunnable(QuestResetDelay delay, boolean reschedule) {
        return () -> {

            try {
                final DeleteBuilder<LoadedQuest, String> builder = instance.getDatabaseManager().getLoadedQuest().getDao().deleteBuilder();
                builder.setWhere(builder.where().eq("quest-delay-type", delay));
                builder.delete();
            } catch (SQLException e) {
                instance.getLogger().warning("An error occurred while deleting loaded quests: " + delay);
                return;
            }

            final int max = instance.getConfig().getInt("max-quests." + delay.name().toLowerCase());
            instance.getQuestManager().removeLoadedQuest(delay);
            instance.getQuestManager().fillLoadedQuest(delay, max);

            instance.getSaveTask().run();

            if(reschedule) {
                this.scheduleNextReset(delay);
            }
        };
    }
}
