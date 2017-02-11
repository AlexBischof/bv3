package de.bischinger.buchungstool.boundary;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.bischinger.buchungstool.model.Warning.Typ;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class Event {

    private final long start;
    private final long end;
    private final CharSequence title;
    private String color;
    private boolean allDay;
    private String url;

    public Event(long start, long end, CharSequence title) {
        this.start = start;
        this.end = end;
        this.title = title;
    }

    @JsonProperty
    public Boolean getAllDay() {
        return allDay;
    }

    public Event withWarningtyp(Typ warningtyp) {
        this.allDay = true;
        this.color = Typ.Min.equals(warningtyp) ? "orange" : "gold";
        return this;
    }

    public String getUrl() {
        return allDay ? null : "/hiwi.xhtml?name="+title;
    }


    @JsonProperty
    public String getColor() {
        return color;
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

