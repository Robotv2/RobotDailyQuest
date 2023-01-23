package fr.robotv2.robotdailyquests.enums;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

    public long milli() {
        return Duration.ofDays(day).toMillis();
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
        };

        time = time.minusMinutes(59).minusSeconds(59).minus(999, ChronoUnit.MICROS);
        return time;
    }

    public long nextResetToEpochMilli() {
        return nextReset().toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
