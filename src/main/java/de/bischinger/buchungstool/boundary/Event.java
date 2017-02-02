package de.bischinger.buchungstool.boundary;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

public class Event {

    private final long start;
    private final long end;
    private final CharSequence title;

    public Event(long start, long end, CharSequence title) {
        this.start = start;
        this.end = end;
        this.title = title;
    }

    @JsonProperty
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    public Date getStart() {
        return new Date(start);
    }

    @JsonProperty
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    public Date getEnd() {
        return new Date(end);
    }

    @JsonProperty
    public CharSequence getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Event{" +
                "start=" + start +
                ", end=" + end +
                ", title=" + title +
                '}';
    }
}

