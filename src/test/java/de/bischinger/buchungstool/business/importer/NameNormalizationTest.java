package de.bischinger.buchungstool.business.importer;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bischofa on 28/06/16.
 */
public class NameNormalizationTest {

    @Test
    public void testApply() throws Exception {
        NameNormalization nameNormalization = new NameNormalization();

        assertThat(nameNormalization.apply("Alex")).isEqualTo("Alex");
        assertThat(nameNormalization.apply("Alex (BO-Hiwi 15-16:00)")).isEqualTo("Alex");
        assertThat(nameNormalization.apply("Alex bei Jochen")).isEqualTo("Alex");
    }
}