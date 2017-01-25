package de.bischinger.buchungstool.business.importer;

import de.bischinger.buchungstool.model.Hiwi;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bischofa on 28/06/16.
 */
public class NameToHiwiMapperTest {

    private NameToHiwiMapper nameToHiwiMapper;

    @Before
    public void before() {
        nameToHiwiMapper = new NameToHiwiMapper();
    }

    @Test
    public void testOneUserCreated() throws Exception {
        List<Hiwi> hiwis = nameToHiwiMapper.apply("Alex");
        assertThat(hiwis).hasSize(1);
        assertThat(hiwis.get(0).getName()).isEqualTo("Alex");

        assertThat(nameToHiwiMapper.apply("Alex (HvD)").get(0).getName()).isEqualTo("Alex");
        assertThat(nameToHiwiMapper.apply("Alex (krank)").get(0).getName()).isEqualTo("Alex");
        assertThat(nameToHiwiMapper.apply("Alex (krank Att)").get(0).getName()).isEqualTo("Alex");
    }

    @Test
    public void testTwoUserCreatedByPairing() throws Exception {
        List<Hiwi> hiwis = nameToHiwiMapper.apply("Alex bei Jochen");

        assertThat(hiwis).hasSize(2);
        assertThat(hiwis.get(0).getName()).isEqualTo("Alex");
        assertThat(hiwis.get(1).getName()).isEqualTo("Jochen");

    }

    @Test
    public void testTwoUserCreatedByIll() throws Exception {
        List<Hiwi> hiwis = nameToHiwiMapper.apply("Jochen (für Alex krank)");
        assertThat(hiwis).hasSize(2);
        assertThat(hiwis.get(0).getName()).isEqualTo("Jochen");
        assertThat(hiwis.get(1).getName()).isEqualTo("Alex");
    }

    @Test
    public void testTwoUserCreatedByVertretung() throws Exception {
        List<Hiwi> hiwis = nameToHiwiMapper.apply("Katharina J. (für Anja)");
        assertThat(hiwis).hasSize(2);
        assertThat(hiwis.get(0).getName()).isEqualTo("Katharina J.");
        assertThat(hiwis.get(1).getName()).isEqualTo("Anja");
    }
}