package de.bischinger.buchungstool.business.importer;

import de.bischinger.buchungstool.business.IllegalTimeException;
import de.bischinger.buchungstool.business.NettoDurationFunction;
import de.bischinger.buchungstool.model.BookingTyp;
import de.bischinger.buchungstool.model.User;
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
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.time.LocalDateTime.ofInstant;
import static java.util.Collections.singletonList;
import static java.util.logging.Logger.getAnonymousLogger;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static net.fortuna.ical4j.model.Property.*;

/**
 * Created by bischofa on 28/06/16.
 */
public class IcsImporter {

    private static final List<String> skipList = of("teambesprechung*,zsb*,geburtstag*,schulung,sis ge*,feiertag*,*sissis".split(","))
            .map(s -> s.replaceAll("\\*", ".*")).collect(toList());

    private final File file;

    public IcsImporter(File file) {
        this.file = file;
    }

    public ImportResult importFile() throws IcsFileReadException {

        Function<File, ComponentList> importFunction = this::importIcsFile;

        ImportResult importResult = new ImportResult();

        //Sammelt zunächst alle Recurrences zusammen, weil diese beliebig kommen können
        Map<String, List<Tuple>> recurrenceIdMap = new HashMap<>();
        importFunction.apply(file).stream().filter(hasProperty(RECURRENCE_ID)).forEach(o -> {
            try {
                Component c = (Component) o;
                Tuple newRecurrenceIdTuple = new Tuple(getStart(c), getEnd(c));
                List<Tuple> localDateTimes = recurrenceIdMap.putIfAbsent(recurrenceIdKey(c), new ArrayList<>(singletonList(newRecurrenceIdTuple)));
                if (localDateTimes != null) {
                    localDateTimes.add(newRecurrenceIdTuple);
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
                                .and(c -> skipList.stream()
                                        .noneMatch(name -> getName(c).trim().toLowerCase().matches(name)))));

        NameToUserFunction nameToUserFunction = new NameToUserFunction();
        BookingTypMapping bookingTypMapping = new BookingTypMapping();

        Stream<Component> componentStream = prefilterComponents.apply(file);

        Stream<User> userStream = componentStream.flatMap(component -> {
            String originalName = getName(component).trim();

            //Extract 1 or 2 Users (e.g. pairing or ill)
            List<User> users = nameToUserFunction.apply(originalName);

            //Sets time for each user (either from series or directly from component)
            users.forEach(user -> {
                Predicate<Component> isSeriesRule = hasProperty(RRULE);

                BookingTyp[] bookingTyps = bookingTypMapping.apply(user.getOriginalName());

                try {
                    if (isSeriesRule.test(component)) {
                        new RecurrenceDateHandler(component)
                                .getEvents(recurrenceIdMap, bookingTypMapping, user)
                                .forEach(importResult::addEvent);
                    } else {
                        LocalDateTime toDate = getEnd(component);
                        LocalDateTime fromDate = getStart(component);
                        try {
                            user.addTimes(fromDate, toDate, bookingTyps);
                            importResult.addEvent(getStartAsDate(component), getEndAsDate(component), originalName);
                        } catch (IllegalTimeException e) {
                            importResult.addError(user, fromDate, toDate, e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    getAnonymousLogger().warning(e.getMessage());
                }
            });

            return users.stream();
        });

        NettoDurationFunction nettoDurationFunction = new NettoDurationFunction();

        //Merge same users
        Stream<User> mergedUser = userStream.collect(groupingBy(User::getName)).entrySet().stream()
                .map(stringListEntry -> stringListEntry.getValue().stream().reduce((user, user2) -> {
                            user.addUser(user2);
                            return user;
                        })
                )
                .map(Optional::get)
                .peek(user -> {//Self-Check
                    if (user.getScheduleMap().isEmpty()) {
                        throw new RuntimeException("Empty Schedule found for " + user);
                    }
                }).peek(user -> user.calcDurations(nettoDurationFunction));

        importResult.addUsers(mergedUser.collect(toList()));

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
    static class Tuple {
        public final LocalDateTime startTime;
        public final LocalDateTime endTime;

        Tuple(LocalDateTime startTime, LocalDateTime endTime) {
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
            return "Tuple{" +
                    "startTime=" + startTime +
                    ", endTime=" + endTime +
                    '}';
        }
    }
}
