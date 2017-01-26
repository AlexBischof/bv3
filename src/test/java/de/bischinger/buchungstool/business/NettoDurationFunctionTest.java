package de.bischinger.buchungstool.business;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bischofa on 28/06/16.
 */
public class NettoDurationFunctionTest {

    @Test
    public void testApply() throws Exception {
        Integer netto = new NettoDurationFunction().apply(241);
        assertThat(netto).isEqualTo(211);

        netto = new NettoDurationFunction().apply(240);
        assertThat(netto).isEqualTo(240);
    }
}