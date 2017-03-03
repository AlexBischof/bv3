package de.bischinger.buchungstool.model;

import de.bischinger.buchungstool.business.NettoDurationFunction;
import de.bischinger.buchungstool.jsf.BookingDto;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.format.TextStyle.FULL;
import static java.util.Comparator.comparing;
import static java.util.Locale.GERMAN;
import static java.util.Map.Entry;
import static java.util.stream.Collectors.*;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

/**
 * Created by bischofa on 28/06/16.
 */
@Entity
public class Hiwi extends RootPojo {
    private static final long serialVersionUID = -7130519343755801047L;

    private String name;
    private String originalName;

    @OneToMany(cascade = {PERSIST, REMOVE}, fetch = EAGER, orphanRemoval = true)
    private Map<LocalDate, Schedule> scheduleMap;
    @Transient
    private Map<LocalDate, Double> bookingsPerDate;

    public Hiwi() {
    }

    public Hiwi(String name, String originalName) {
        this.name = name;
        this.originalName = originalName;
    }

    public String getName() {
        return name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void addHiwi(Hiwi other) {
        other.getScheduleMap().forEach((otherLocalDate, otherSchedule) ->
        {
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
        return "Hiwi{" +
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
        getScheduleMap().values().forEach(schedule -> schedule.getBookingList().forEach(booking ->
        {
            booking.calcDuration(durationFunction);
        }));
    }

    public Map<LocalDate, Double> collectBookingsByDate() {
        if (bookingsPerDate == null) {
            bookingsPerDate = this.getScheduleMap().entrySet().stream()
                    //collects bookings per date and ignores ill
                    .map(e -> new NettoMinutesPerLocalDate(e.getKey(), e.getValue().getBookingList().stream().
                            filter(Booking::isNotIll).mapToInt(Booking::getNettoDuration).sum()))
                    .collect(groupingBy(NettoMinutesPerLocalDate::getLocalDate,
                            mapping(NettoMinutesPerLocalDate::getNetto, summingDouble(i -> i))));
        }
        return bookingsPerDate;
    }

    public double getOverallNetto() {
        return collectBookingsByDate().values().stream().mapToDouble(e -> e).sum();
    }

    public Map<Month, Double> getMonthlyNetto() {
        return collectBookingsByDate().entrySet().stream()
                .collect(
                        groupingBy(e -> e.getKey().getMonth(),
                                mapping(Entry::getValue, summingDouble(i -> i))));
    }

    public String getMonthlyNettoAsString() {
        return getMonthlyNetto().entrySet().stream()
                .sorted(comparing(Entry::getKey))
                .map(e -> e.getKey().getDisplayName(FULL, GERMAN) + ": " + e.getValue()).
                        collect(joining(", "));
    }

    public Map<Integer, Double> getWeeklyNetto() {
        TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfYear();
        return collectBookingsByDate().entrySet().stream()
                .collect(
                        groupingBy(e -> e.getKey().get(weekOfYear),
                                mapping(Entry::getValue, summingDouble(i -> i))));
    }

    private static class NettoMinutesPerLocalDate {
        private final LocalDate localDate;
        private final double netto;

        NettoMinutesPerLocalDate(LocalDate localDate, int netto) {
            this.localDate = localDate;
            this.netto = ((double) netto) / 60d;
        }

        LocalDate getLocalDate() {
            return localDate;
        }

        double getNetto() {
            return netto;
        }
    }

    public List<BookingDto> getBookings()
    {
        DateTimeFormatter dateTimeFormatter = ofPattern("dd.MM.yyyy");
        return this.getScheduleMap().entrySet().stream()
            .sorted(comparing(Map.Entry::getKey))
            .flatMap(e -> e.getValue().getBookingList().stream().map(b ->
                new BookingDto(dateTimeFormatter.format(e.getKey()), b.getFrom(), b.getTo(), b.getNettoDuration(),
                    b.getBruttoDuration())
            ))
            .collect(toList());
    }
}
