package de.bischinger.buchungstool.model;

import de.bischinger.buchungstool.business.NettoDurationFunction;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bischofa on 28/06/16.
 */
public class User implements Serializable {
    private String name;
    private String originalName;
    private Map<LocalDate, Schedule> scheduleMap;

    public User() {
    }

    public User(String name, String originalName) {
        this.name = name;
        this.originalName = originalName;
    }

    public String getName() {
        return name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void addUser(User other) {
        other.getScheduleMap().forEach((otherLocalDate, otherSchedule) -> {

            Schedule curSchedule = getScheduleMap().get(otherLocalDate);
            if (curSchedule == null) {
                getScheduleMap().put(otherLocalDate, otherSchedule);
            } else {
                curSchedule.addBookingList(otherSchedule.getBookingList());
            }
        });
    }

    public Map<LocalDate, Schedule> getScheduleMap() {
        if (scheduleMap == null) {
            scheduleMap = new HashMap<>();
        }
        return scheduleMap;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", originalName='" + originalName + '\'' +
                ", scheduleMap=" + scheduleMap +
                '}';
    }

    public void addTimes(LocalDateTime fromDate, LocalDateTime toDate, BookingTyp[] bookingTyps) {
        LocalDate localDate = fromDate.toLocalDate();
        LocalTime fromTime = fromDate.toLocalTime();
        LocalTime toTime = toDate.toLocalTime();

        Schedule schedule = getScheduleMap().computeIfAbsent(localDate, k -> new Schedule());
        schedule.addBooking(fromTime, toTime, bookingTyps);
    }

    public void calcDurations(NettoDurationFunction durationFunction) {
        getScheduleMap().values().forEach(schedule -> schedule.getBookingList().forEach(booking -> {
            booking.calcDuration(durationFunction);
        }));
    }
}
