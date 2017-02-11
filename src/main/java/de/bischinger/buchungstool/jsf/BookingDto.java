package de.bischinger.buchungstool.jsf;

import java.time.LocalTime;

import static de.bischinger.buchungstool.business.TimeNumberListFunction.getLocalTime;

public class BookingDto {
    private String date;
    private LocalTime start;
    private LocalTime ende;
    private double bruttoDuration;
    private double nettoDuration;

    public BookingDto(String date, int from, int to, int nettoDuration, int bruttoDuration) {
        this.date = date;
        this.start = getLocalTime(from);
        this.ende = getLocalTime(to);
        this.nettoDuration = nettoDuration / 60d;
        this.bruttoDuration = bruttoDuration / 60d;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnde() {
        return ende;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public void setEnde(LocalTime ende) {
        this.ende = ende;
    }

    public double getBruttoDuration() {
        return bruttoDuration;
    }

    public void setBruttoDuration(int bruttoDuration) {
        this.bruttoDuration = bruttoDuration;
    }

    public double getNettoDuration() {
        return nettoDuration;
    }

    public void setNettoDuration(int nettoDuration) {
        this.nettoDuration = nettoDuration;
    }
}