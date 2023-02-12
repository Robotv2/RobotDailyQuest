package fr.robotv2.robotdailyquests.util;

import fr.robotv2.robotdailyquests.enums.QuestResetDelay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class DateUtil {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static String getDateFormatted(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String getDateFormatted(long timestamp) {
        return DateUtil.getDateFormatted(new Date(timestamp));
    }

    public static String getDateFormatted(QuestResetDelay delay) {
        return DateUtil.getDateFormatted(delay.nextResetToEpochMilli());
    }

    public static long getTimeUntil(QuestResetDelay delay) {
        return delay.nextResetToEpochMilli() - System.currentTimeMillis();
    }

    public static String getTimeUntilFormatted(QuestResetDelay delay) {
        final Duration duration = Duration.ofMillis(DateUtil.getTimeUntil(delay));
        final long hours = duration.toHours();
        final long minutes = duration.minusHours(hours).toMinutes();
        return String.format("%dh%dmin(s)", hours, minutes);
    }
}
