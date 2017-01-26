package de.bischinger.buchungstool.jsf;

import java.time.LocalDate;
import java.time.LocalTime;

import static de.bischinger.buchungstool.business.TimeNumberListFunction.getLocalTime;
import static de.bischinger.buchungstool.model.Warning.Typ;
import static de.bischinger.buchungstool.model.Warning.Typ.Max;

public class WarningDto {
    private String date;
    private LocalTime start;
    private LocalTime ende;
    private String typ;
    private int capacity;
    private int count;

    public WarningDto(String date, int from, int to, Typ typ, int capacity, int count) {
        this.date = date;
        this.start = getLocalTime(from);
        this.ende = getLocalTime(to);
        this.typ = typ.equals(Max) ? "Überbelegung" : "Unterbelegung";
        this.capacity = capacity;
        this.count = count;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnde() {
        return ende;
    }

    public String getTyp() {
        return typ;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public void setEnde(LocalTime ende) {
        this.ende = ende;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}