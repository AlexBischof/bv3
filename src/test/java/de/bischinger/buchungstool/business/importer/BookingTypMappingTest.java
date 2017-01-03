package de.bischinger.buchungstool.business.importer;

import org.junit.Test;

import static de.bischinger.buchungstool.model.BookingTyp.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bischofa on 28/06/16.
 */
public class BookingTypMappingTest {

    @Test
    public void testApply() throws Exception {
        BookingTypMapping bookingTypMapping = new BookingTypMapping();

        assertThat(bookingTypMapping.apply("Alex")).isNull();
        assertThat(bookingTypMapping.apply("Alex bei Jochen")).containsOnly(Pairing);
        assertThat(bookingTypMapping.apply("Alex (HvD)")).containsOnly(HvD);
        assertThat(bookingTypMapping.apply("Jochen (krank)")).containsOnly(Ill);
        assertThat(bookingTypMapping.apply("Jochen (für Alex krank)")).containsOnly(Ill);
        assertThat(bookingTypMapping.apply("Jochen (krank Att)")).containsOnly(IllWithAttest);
        assertThat(bookingTypMapping.apply("Jochen (für Alex krank Att)")).containsOnly(IllWithAttest);

        assertThat(bookingTypMapping.apply("Jochen bei Karin (für Alex krank Att)")).containsOnly(IllWithAttest, Pairing);
    }
}