package de.bischinger.buchungstool.business;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.time.LocalTime.of;
import static java.time.temporal.ChronoUnit.MINUTES;

/**
 * Created by Alex Bischof on 17.05.2016.
 */
public class TimeNumberListFunction implements Function<LocalTime, Integer> {
    private static Map<Object, Integer> timeNumberMap;
    public final static int NUMBER_OF_SLOTS = 31;

    static {
        timeNumberMap = new HashMap<>(NUMBER_OF_SLOTS);

        // 0 -> 8.30
        // 30 -> 16.00
        // which means 16 - 9 = 7 -> 7*4=28 + 8.30 (1) + 8.45 (1) ==> 30
        LocalTime time = of(8, 30);
        for (int i = 0; i < NUMBER_OF_SLOTS; i++) {
            timeNumberMap.put(time, i);
            time = time.plus(15, MINUTES);
        }
    }

    public int getCount() {
        return timeNumberMap.size();
    }

    public static LocalTime getLocalTime(int index) {
        return of(8, 30).plus(15 * index, MINUTES);
    }

    public static int getNumber(LocalTime time) {
        try {
            return timeNumberMap.get(time);
        } catch (NullPointerException e) {
            throw new IllegalTimeException("Nicht definierte Zeit: " + time);
        }
    }

    @Override
    public Integer apply(LocalTime localTime) {
        return timeNumberMap.get(localTime);
    }
}
