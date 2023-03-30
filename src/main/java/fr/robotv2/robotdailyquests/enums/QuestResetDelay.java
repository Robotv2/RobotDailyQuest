package fr.robotv2.robotdailyquests.enums;

import fr.robotv2.robotdailyquests.RobotDailyQuest;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public enum QuestResetDelay {

    DAILY(1),
    WEEKLY(7),
    MONTHLY(30),
    ;

    public static final QuestResetDelay[] VALUES = QuestResetDelay.values();

    private final int day;
    QuestResetDelay(int day) {
        this.day = day;
    }

    public int getMax(QuestDifficulty difficulty) {
        return RobotDailyQuest.get()
                .getConfig()
                .getInt("max-quests." + this.name().toLowerCase() + "." + difficulty.name().toLowerCase(), 0);
    }

    public LocalDateTime nextReset() {

        LocalDateTime time = LocalDateTime.now();

        switch (this) {
            case DAILY -> time = time.plusDays(this.day).truncatedTo(ChronoUnit.DAYS);
            case WEEKLY -> {
                time = time.plusDays(this.day);

                while (time.getDayOfWeek() != DayOfWeek.MONDAY) {
                    time = time.minusDays(1);
                }

                time = time.truncatedTo(ChronoUnit.DAYS);
            }
            case MONTHLY -> time = time.plusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        }

        time = time.plusSeconds(1);  // Just to be sure we're a doing this the right day.
        return time;
    }

    public long nextResetToEpochMilli() {
        return nextReset().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
