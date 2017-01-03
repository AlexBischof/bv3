package de.bischinger.buchungstool.business.importer;

import java.io.Serializable;
import java.util.Date;


/**
 * Created by bischofa on 03/07/16.
 */
public class Event implements Serializable {

    private long start;
    private long end;
    private CharSequence title;

    public Event() {
    }

    public Event(Date start, Date end, CharSequence title) {
        this.start = start.getTime();
        this.end = end.getTime();
        this.title = title;
    }

    public Date getStart() {
        return new Date(start);
    }

    public void setStart(long start) {
        this.start = start;
    }

    public Date getEnd() {
        return new Date(end);
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Event{" +
                "start=" + new Date(start) +
                ", end=" + new Date(end) +
                ", title=" + title +
                '}';
    }
}
