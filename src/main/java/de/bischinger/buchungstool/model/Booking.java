package de.bischinger.buchungstool.model;

import de.bischinger.buchungstool.business.NettoDurationFunction;
import de.bischinger.buchungstool.business.TimeNumberListFunction;

import java.time.LocalTime;
import java.util.Arrays;

import static de.bischinger.buchungstool.model.BookingTyp.Ill;
import static java.util.Arrays.binarySearch;

/**
 * Created by bischofa on 28/06/16.
 */
public class Booking {
    private int from;
    private int to;
    private BookingTyp[] bookingTyp;
    private int bruttoDuration;
    private int nettoDuration;

    public Booking(LocalTime from, LocalTime to, BookingTyp[] typ) {
        this.from = TimeNumberListFunction.getNumber(from);
        this.to = TimeNumberListFunction.getNumber(to);
        this.bookingTyp = typ;
    }

    public void calcDuration(NettoDurationFunction durationFunction) {
        this.bruttoDuration = (int) ((to - from) / 4d * 60);
        this.nettoDuration = durationFunction.apply(bruttoDuration);
    }

    public int getBruttoDuration() {
        return bruttoDuration;
    }

    public int getNettoDuration() {
        return nettoDuration;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public boolean isNotIll() {
        return bookingTyp == null || binarySearch(bookingTyp, Ill) < 0;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "from=" + from +
                ", to=" + to +
                ", bookingTyp=" + Arrays.toString(bookingTyp) +
                ", bruttoDuration=" + bruttoDuration +
                ", nettoDuration=" + nettoDuration +
                '}';
    }
}
