package de.bischinger.buchungstool.model;

import java.io.Serializable;
import java.time.LocalDate;

import static de.bischinger.buchungstool.business.TimeNumberListFunction.getLocalTime;

/**
 * Created by bischofa on 28/06/16.
 */
public class Warning implements Serializable {

    private LocalDate date;
    private int from;
    private int to;
    private Typ typ;
    private int capacity;
    private int count;

    public enum Typ {
        Min, Max
    }

    public Warning(LocalDate date, int from, int to, Typ typ, int capacity, int count) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.typ = typ;
        this.capacity = capacity;
        this.count = count;

    }

    public LocalDate getDate() {
        return date;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public Typ getTyp() {
        return typ;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Warning warning = (Warning) o;

        if (from != warning.from) return false;
        if (to != warning.to) return false;
        if (count != warning.count) return false;
        if (!date.equals(warning.date)) return false;
        return typ == warning.typ;

    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + from;
        result = 31 * result + to;
        result = 31 * result + typ.hashCode();
        result = 31 * result + count;
        return result;
    }

    @Override
    public String toString() {
        return date + ": " + (typ.equals(Typ.Max) ? "Überbuchung" : "Unterbelegt") + " (" + capacity + ") "
                + getLocalTime(from) + " - " + getLocalTime(to + 1);
    }
}
