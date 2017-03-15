package de.bischinger.buchungstool.jsf;

import de.bischinger.buchungstool.model.Booking;
import de.bischinger.buchungstool.model.Hiwi;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.format.TextStyle.FULL;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;
import static java.time.temporal.WeekFields.ISO;
import static java.util.Comparator.comparing;
import static java.util.Locale.GERMAN;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.summingInt;

/**
 * Created by Alex Bischof on 25.01.2017.
 */
@ManagedBean
@RequestScoped
public class HiwiDetailBean {
    @ManagedProperty(value = "#{param.name}")
    private String name;

    private Hiwi hiwi;

    @Inject
    private HiwiListProcucer hiwiListProcucer;

    @PostConstruct
    public void init() {
        Optional<Hiwi> hiwiOptional = hiwiListProcucer.getHiwis().stream().filter(hiwi -> hiwi.getName().equals(name))
                .findFirst();
        if (hiwiOptional.isPresent()) {
            this.hiwi = hiwiOptional.get();
            this.name = hiwi.getName();
        } else {
            this.name = "Hiwi nicht vorhanden";
        }
    }

    public String getName() {
        return name;
    }

    public String getBookingDates() {
        if (hiwi == null) {
            return "";
        }

        DateTimeFormatter dateTimeFormatter = ofPattern("dd.MM.yyyy");
        return hiwi.getScheduleMap().keySet().stream().sorted()
                .map(dateTimeFormatter::format)
                .map(date -> "\"" + date + "\"")
                .collect(joining(","));
    }

    public String getBookingValuesNetto() {
        if (hiwi == null) {
            return "";
        }

        return calculateValuesInternal(Booking::getNettoDuration);
    }

    public String getBookingValuesBrutto() {
        if (hiwi == null) {
            return "";
        }
        return calculateValuesInternal(Booking::getBruttoDuration);
    }

    public String getBookingValuesNettoWeeks() {
        if (hiwi == null) {
            return "";
        }

        DateTimeFormatter dateTimeFormatter = ofPattern("dd.MM");
        return hiwi.getWeeklyNetto().keySet().stream().sorted()
                .map(week -> {
                    LocalDate dateOfWeek = now().with(ISO.weekOfWeekBasedYear(), week);
                    return format("\"%s-%s\"", dateOfWeek.with(previousOrSame(MONDAY)).format(dateTimeFormatter),
                            dateOfWeek.with(nextOrSame(FRIDAY)).format(dateTimeFormatter));
                })
                .collect(joining(","));
    }

    public String getBookingValuesNettoWeekValues() {
        if (hiwi == null) {
            return "";
        }
        return hiwi.getWeeklyNetto().entrySet().stream()
                .sorted(comparing(Entry::getKey))
                .map(Map.Entry::getValue)
                .map(v -> "\"" + v + "\"")
                .collect(joining(","));
    }

    public String getBookingValuesNettoMonthly() {
        if (hiwi == null) {
            return "";
        }
        return hiwi.getMonthlyNetto().keySet().stream().sorted()
                .map(month -> month.getDisplayName(FULL, GERMAN))
                .map(v -> "\"" + v + "\"")
                .collect(joining(","));
    }

    public String getBookingValuesNettoMonthlyValues() {
        if (hiwi == null) {
            return "";
        }
        return hiwi.getMonthlyNetto().entrySet().stream()
                .sorted(comparing(Entry::getKey))
                .map(Map.Entry::getValue)
                .map(v -> "\"" + v + "\"")
                .collect(joining(","));
    }

    private String calculateValuesInternal(Function<Booking, Integer> function) {
        return hiwi.getScheduleMap().entrySet().stream()
                .sorted(comparing(Entry::getKey))
                .map(e -> e.getValue().getBookingList().stream().map(function).collect(summingInt(i -> i)))
                .map(h -> h / 60d)
                .map(date -> "\"" + date + "\"")
                .collect(joining(","));
    }

    public void setName(String name) {
        this.name = name;
    }

}
