package de.bischinger.buchungstool.business;

import org.junit.Test;

import java.time.LocalTime;

import static java.time.LocalTime.of;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bischofa on 28/06/16.
 */
public class TimeNumberListFunctionTest {

    private TimeNumberListFunction suT = new TimeNumberListFunction();

    @Test
    public void testGet() throws Exception {
        assertThat(suT.getCount()).isEqualTo(51);
    }

    @Test
    public void testApply() throws Exception {
        LocalTime eight = of(8, 30);
        LocalTime nine = of(9, 00);
        LocalTime sixteen = of(16, 00);
        LocalTime twenty = of(20, 00);

        assertThat(suT.apply(eight)).isEqualTo(0);
        assertThat(suT.apply(nine)).isEqualTo(2);
        assertThat(suT.apply(sixteen)).isEqualTo(30);
        assertThat(suT.apply(twenty)).isEqualTo(46);
    }
}