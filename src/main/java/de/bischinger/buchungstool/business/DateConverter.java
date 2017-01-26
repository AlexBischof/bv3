package de.bischinger.buchungstool.business;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.of;

/**
 * Created by Alexander Bischof on 21.08.15.
 */
public class DateConverter {

    public static LocalDateTime convertTo(Calendar calendar) {
        return ofInstant(ofEpochMilli(calendar.getTimeInMillis()), of("Europe/Berlin"));
    }

    public static LocalDateTime convertTo(Date date) {
        return ofInstant(date.toInstant(), of("Europe/Berlin"));
    }

}
