package de.bischinger.buchungstool.business.importer;

import de.bischinger.buchungstool.model.Hiwi;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.util.Dates;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static de.bischinger.buchungstool.business.DateConverter.convertTo;
import static de.bischinger.buchungstool.business.importer.IcsImporter.*;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static net.fortuna.ical4j.model.Property.EXDATE;
import static net.fortuna.ical4j.model.Property.RRULE;
import static net.fortuna.ical4j.model.parameter.Value.DATE_TIME;
import static org.apache.commons.lang.time.DateUtils.addWeeks;

/**
 * Created by bischofa on 03/01/17.
 */
class RecurrenceDateHandler {

    private static final List<String> SUPPORTED_RECCURENCE_TYPES = asList("WEEKLY", "YEARLY", "DAILY");

    private final Component component;
    private final RRule rrule;
    private final Recur recur;

    public RecurrenceDateHandler(Component component) {
        this.component = component;

        rrule = (RRule) component.getProperties(RRULE).get(0);
        recur = rrule.getRecur();
        String frequency = recur.getFrequency();
        if (!SUPPORTED_RECCURENCE_TYPES.contains(frequency)) {
            throw new RuntimeException(
                    "Serientyp wird nicht unterstützt:" + frequency + " " + component.toString());
        }
    }

    public Stream<Event> getEvents(Map<String, List<IcsImporter.LocalDateTimePeriod>> recurrenceIdMap, BookingTypMapping bookingTypMapping, Hiwi hiwi) {
        Date rStart = getStartAsDate(component);
        Date rEnd = getEndAsDate(component);
        Date periodEnd = rEnd;

        //Check for no interval set
        if (recur.getInterval() == -1) {
            java.util.Date newDate = addWeeks(rEnd, recur.getCount());
            periodEnd = new DateTime(newDate.getTime());
        }

        //Holt alle Dates zur Recurrence (inkl. Abweichungen aka EXDATES)
        DateList dates = recur.getDates(rStart, rStart, periodEnd, DATE_TIME, recur.getCount());

        //ExDates definieren zur Serie ein abweichendes Startdatum
        PropertyList exdateProperty = component.getProperties(EXDATE);
        final DateList exdates = exdateProperty == null || exdateProperty.isEmpty() ? null : ((ExDate) exdateProperty.get(0)).getDates();

        return dates.stream().map(icsDate -> {

            java.util.Date date = (java.util.Date) icsDate;
            LocalDateTime fromDate = convertTo(date);

            LocalDateTime tmpEnd = getEnd(component);
            LocalDateTime originalEnd = fromDate.minusHours(0).with(HOUR_OF_DAY, tmpEnd.getHour()).with(MINUTE_OF_HOUR, tmpEnd.getMinute());
            LocalDateTime calculatedEnd = originalEnd;
            List<IcsImporter.LocalDateTimePeriod> tuples = recurrenceIdMap.get(recurrenceIdKey(component));

            if (tuples != null) {
                Optional<LocalDateTime> first = tuples.stream().map(LocalDateTimePeriod::getEndTime)
                        .filter(localDateTime -> localDateTime.toLocalDate().equals(originalEnd.toLocalDate()))
                        .findFirst();
                if (first.isPresent()) {
                    calculatedEnd = first.get();
                }
            }

            hiwi.addTimes(fromDate, calculatedEnd, bookingTypMapping.apply(hiwi.getName()));
            java.util.Date end = Date.from(calculatedEnd.atZone(ZoneId.of("Europe/Berlin")).toInstant());

            //Wenn in exdate definiert, anpassung startdate
            Object startDate = ofNullable(exdates).map(e -> e.stream()
                    .filter(exdate -> Dates.getInstance((java.util.Date) exdate, DATE_TIME).equals(date))
                    .findFirst().orElse(date)).orElse(date);

            return new Event((java.util.Date) startDate, end, hiwi.getName());
        });
    }
}
