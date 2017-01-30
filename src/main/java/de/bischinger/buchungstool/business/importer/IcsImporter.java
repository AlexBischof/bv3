package de.bischinger.buchungstool.business.importer;

import de.bischinger.buchungstool.business.IllegalTimeException;
import de.bischinger.buchungstool.business.NettoDurationFunction;
import de.bischinger.buchungstool.model.BookingTyp;
import de.bischinger.buchungstool.model.Hiwi;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.time.LocalDateTime.ofInstant;
import static java.util.Collections.singletonList;
import static java.util.logging.Logger.getLogger;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static net.fortuna.ical4j.model.Property.*;

/**
 * Created by bischofa on 28/06/16.
 */
public class IcsImporter {

    private final File file;
    private final boolean isSommer;

    public IcsImporter(File file, boolean isSommer) {
        this.file = file;
        this.isSommer = isSommer;
    }

    public ImportResult importFile(Supplier<List<String>> skipListSupplier) throws IcsFileReadException {

        Function<File, ComponentList> importFunction = this::importIcsFile;

        ImportResult importResult = new ImportResult();

        //Sammelt zunächst alle Recurrences zusammen, weil diese beliebig kommen können
        Map<String, List<LocalDateTimePeriod>> recurrenceIdMap = new HashMap<>();
        importFunction.apply(file).stream().filter(hasProperty(RECURRENCE_ID)).forEach(o -> {
            try {
                Component c = (Component) o;
                LocalDateTimePeriod newRecurrenceIdLocalDateTimePeriod = new LocalDateTimePeriod(getStart(c), getEnd(c));
                List<LocalDateTimePeriod> localDateTimes = recurrenceIdMap.putIfAbsent(recurrenceIdKey(c), new ArrayList<>(singletonList(newRecurrenceIdLocalDateTimePeriod)));
                if (localDateTimes != null) {
                    localDateTimes.add(newRecurrenceIdLocalDateTimePeriod);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //Geht nun alle nicht events durch
        Function<File, Stream<Component>> prefilterComponents = importFunction.andThen(
                componentList -> componentList.stream()
                        .filter(hasProperty(DTSTART)
                                .and(hasProperty(SUMMARY))
                                .and(hasProperty(RECURRENCE_ID).negate())
                                .and(c -> !getName(c).trim().isEmpty())
                                .and(c -> skipListSupplier.get().stream()
                                        .noneMatch(name -> getName(c).trim().toLowerCase().matches(name)))));

        NameToHiwiMapper nameToHiwiMapper = new NameToHiwiMapper();
        BookingTypMapping bookingTypMapping = new BookingTypMapping();

        Stream<Component> componentStream = prefilterComponents.apply(file);

        Stream<Hiwi> HiwiStream = componentStream.flatMap(component -> {
            String originalName = getName(component).trim();

            //Extract 1 or 2 Hiwis (e.g. pairing or ill)
            List<Hiwi> hiwis = nameToHiwiMapper.apply(originalName);

            //Sets time for each Hiwi (either from series or directly from component)
            hiwis.forEach(hiwi -> {
                Predicate<Component> isSeriesRule = hasProperty(RRULE);

                BookingTyp[] bookingTyps = bookingTypMapping.apply(hiwi.getOriginalName());

                try {
                    if (isSeriesRule.test(component)) {
                        new RecurrenceDateHandler(component)
                                .getEvents(recurrenceIdMap, bookingTypMapping, hiwi)
                                .forEach(importResult::addEvent);
                    } else {
                        LocalDateTime toDate = getEnd(component);
                        LocalDateTime fromDate = getStart(component);
                        try {
                            hiwi.addTimes(fromDate, toDate, bookingTyps);
                            importResult.addEvent(getStartAsDate(component), getEndAsDate(component), originalName);
                        } catch (IllegalTimeException e) {
                            importResult.addError(hiwi, fromDate, toDate, e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    getLogger(IcsImporter.class.getSimpleName()).warning(e.getMessage());
                }
            });

            return hiwis.stream();
        });

        NettoDurationFunction nettoDurationFunction = new NettoDurationFunction(() -> isSommer);

        //Merge same Hiwis
        Stream<Hiwi> mergedHiwi = HiwiStream.collect(groupingBy(Hiwi::getName)).entrySet().stream()
                .map(stringListEntry -> stringListEntry.getValue().stream().reduce((hiwi, hiwi2) -> {
                            hiwi.addHiwi(hiwi2);
                            return hiwi;
                        })
                )
                .map(Optional::get)
                .peek(Hiwi -> {//Self-Check
                    if (Hiwi.getScheduleMap().isEmpty()) {
                        throw new RuntimeException("Empty Schedule found for " + Hiwi);
                    }
                }).peek(Hiwi -> Hiwi.calcDurations(nettoDurationFunction));

        importResult.addHiwis(mergedHiwi.collect(toList()));

        return importResult;
    }

    static String recurrenceIdKey(Component component) {
        return getUid(component).getValue();
    }

    static LocalDateTime convertTo(java.util.Date date) {
        return ofInstant(date.toInstant(), ZoneId.of("Europe/Berlin"));
    }

    static Date getEndAsDate(Component component) {
        return ((DateProperty) component.getProperty(DTEND)).getDate();
    }

    static Date getStartAsDate(Component component) {
        return ((DateProperty) component.getProperty(DTSTART)).getDate();
    }

    static Uid getUid(Component component) {
        return (Uid) component.getProperty(UID);
    }

    static LocalDateTime getEnd(Component component) {
        return convertTo(getEndAsDate(component));
    }

    private LocalDateTime getStart(Component component) {
        return convertTo(getStartAsDate(component));
    }

    private String getName(Component component) {
        return ((Summary) component.getProperties(SUMMARY).get(0)).getValue();
    }

    private Predicate<Component> hasProperty(String property) {
        return component -> component != null && !component.getProperties(property).isEmpty();
    }

    private ComponentList importIcsFile(File file) {
        return new IcsFileReader().read(file).getComponents();
    }

    //FIXME scala case class
    static class LocalDateTimePeriod {
        public final LocalDateTime startTime;
        public final LocalDateTime endTime;

        LocalDateTimePeriod(LocalDateTime startTime, LocalDateTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public LocalDateTime getEndTime() {
            return endTime;
        }

        @Override
        public String toString() {
            return "LocalDateTimePeriod{" +
                    "startTime=" + startTime +
                    ", endTime=" + endTime +
                    '}';
        }
    }
}
