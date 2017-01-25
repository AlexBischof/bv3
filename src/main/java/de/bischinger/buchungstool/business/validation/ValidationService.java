package de.bischinger.buchungstool.business.validation;

import de.bischinger.buchungstool.model.Booking;
import de.bischinger.buchungstool.model.Capacity;
import de.bischinger.buchungstool.model.Hiwi;
import de.bischinger.buchungstool.model.Warning;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static de.bischinger.buchungstool.business.TimeNumberListFunction.NUMBER_OF_SLOTS;
import static de.bischinger.buchungstool.model.Warning.Typ.Max;
import static de.bischinger.buchungstool.model.Warning.Typ.Min;
import static java.util.stream.Collectors.*;

/**
 * Created by bischofa on 28/06/16.
 */
public class ValidationService {

    private final Map<LocalDate, Integer> dateCapacity;
    private final int defaultCapacity;
    private final int minBooking;

    public ValidationService(List<Capacity> capacities, int defaultCapacity, int minBooking) {
        this.dateCapacity = capacities.stream().collect(toMap(Capacity::getDate, Capacity::getNumber));
        this.defaultCapacity = defaultCapacity;
        this.minBooking = minBooking;
    }

    public List<Warning> validate(Stream<Hiwi> userStream) {

        //filter by day
        Map<LocalDate, List<Entry>> bookingsPerDay = userStream
                .flatMap(user -> user.getScheduleMap()
                        .entrySet()
                        .stream()
                        .map(localDateScheduleEntry -> new Entry(user.getName(), localDateScheduleEntry.getKey(), localDateScheduleEntry.getValue().getBookingList())))
                .collect(groupingBy(Entry::getLocalDate));

        List<Warning> warnings = new ArrayList<>();

        bookingsPerDay.forEach((localDate, entries) -> {
            Integer capacity = dateCapacity.getOrDefault(localDate, defaultCapacity);

            //Maps slots to count
            Map<Integer, Integer> countMap = new HashMap<>();

            //BitSet anhand länge absteigend sortieren
            entries.forEach(entry -> entry.getBookingsAsBitSet().stream()
                    .flatMapToInt(BitSet::stream)
                    .forEach(bit -> countMap.merge(bit, 1, (oldValue, newValue) -> oldValue == null ? newValue : oldValue + newValue)));

            Warning lastWarning = null;
            for (Map.Entry<Integer, Integer> e : countMap.entrySet().stream()
                    .filter(k -> k.getKey() > 1)    //warnings erst ab 9 Uhr betrachten
                    .sorted((o1, o2) -> Integer.compare(o1.getKey(), o2.getKey()))
                    .collect(toList())) {
                Integer slot = e.getKey();
                Integer count = e.getValue();

                if (count < minBooking || count > capacity) {

                    Warning.Typ typ = count < minBooking ? Min : Max;
                    int diff = typ.equals(Min) ? minBooking - count : count - capacity;

                    Warning warning = null;
                    if (lastWarning == null) {
                        lastWarning = warning = new Warning(localDate, slot, slot, typ, capacity, diff);
                        warnings.add(warning);
                    } else {

                        //Check typ and dist to to
                        boolean updateLastWarning = lastWarning.getTyp().equals(typ) && slot - lastWarning.getTo() == 1 && lastWarning.getCount() == diff;
                        if (updateLastWarning) {
                            lastWarning.setTo(slot);
                        } else {
                            lastWarning = warning = new Warning(localDate, slot, slot, typ, capacity, diff);
                            warnings.add(warning);
                        }
                    }
                } else {
                    lastWarning = null;
                }
            }
        });

        warnings.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        return warnings;
    }

    static class Entry {
        private String user;
        private LocalDate localDate;
        private List<BitSet> bookingsAsBitSet;

        public Entry(String user, LocalDate localDate, List<Booking> bookings) {
            this.user = user;
            this.localDate = localDate;
            this.bookingsAsBitSet = bookings.stream()
                    .map(b -> {
                        BitSet bitSet = new BitSet(NUMBER_OF_SLOTS);
                        bitSet.set(b.getFrom(), b.getTo(), true);
                        return bitSet;
                    })
                    .collect(toList());
        }

        public String getUser() {
            return user;
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public List<BitSet> getBookingsAsBitSet() {
            return bookingsAsBitSet;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "user='" + user + '\'' +
                    ", localDate=" + localDate +
                    ", bookingsAsBitSet=" + bookingsAsBitSet +
                    '}';
        }
    }
}
