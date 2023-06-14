package fr.robotv2.robotdailyquests.enums;

import fr.robotv2.robotdailyquests.RobotDailyQuest;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
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
        return switch (this) {
            case DAILY -> time.plusDays(this.day).truncatedTo(ChronoUnit.DAYS);
            case WEEKLY -> {
                time = time.plusDays(this.day).with(ChronoField.DAY_OF_WEEK, 1);
                while(time.getDayOfWeek() != DayOfWeek.MONDAY) {
                    time = time.plusDays(1);
                }
                yield time.truncatedTo(ChronoUnit.DAYS);
            }
            case MONTHLY -> time.plusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        };
    }

    public long nextResetToEpochMilli() {
        return nextReset().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
