package de.bischinger.buchungstool.business.importer;

import de.bischinger.buchungstool.model.BookingTyp;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by bischofa on 28/06/16.
 */
class BookingTypMapping implements Function<String, BookingTyp[]> {
    @Override
    public BookingTyp[] apply(String originalName) {

        List<BookingTyp> bookingTyps = new ArrayList<>();

        String normedOriginalName = originalName.toLowerCase();

        boolean ill = normedOriginalName.contains("krank");
        if (ill) {
            if (normedOriginalName.contains(" att")) {
                bookingTyps.add(BookingTyp.IllWithAttest);
            } else {
                bookingTyps.add(BookingTyp.Ill);
            }
        }

        if (normedOriginalName.contains("bei")) {
            bookingTyps.add(BookingTyp.Pairing);
        }

        if (normedOriginalName.contains("hvd")) {
            bookingTyps.add(BookingTyp.HvD);
        }

        return bookingTyps.isEmpty() ? null : bookingTyps.toArray(new BookingTyp[bookingTyps.size()]);
    }
}
