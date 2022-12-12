package fr.robotv2.robotdailyquests.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static String getDateFormatted(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String getDateFormatted(long timestamp) {
        return DateUtil.getDateFormatted(new Date(timestamp));
    }
}
