package de.bischinger.buchungstool.business.importer;

import de.bischinger.buchungstool.model.User;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bischofa on 28/06/16.
 */
public class NameToUserFunctionTest {

    private NameToUserFunction nameToUserFunction;

    @Before
    public void before(){
        nameToUserFunction = new NameToUserFunction();
    }

    @Test
    public void testOneUserCreated() throws Exception {


        List<User> users = nameToUserFunction.apply("Alex");
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("Alex");

        assertThat(nameToUserFunction.apply("Alex (HvD)").get(0).getName()).isEqualTo("Alex");
        assertThat(nameToUserFunction.apply("Alex (krank)").get(0).getName()).isEqualTo("Alex");
        assertThat(nameToUserFunction.apply("Alex (krank Att)").get(0).getName()).isEqualTo("Alex");
    }

    @Test
    public void testTwoUserCreatedByPairing() throws Exception {
        List<User> users = nameToUserFunction.apply("Alex bei Jochen");

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getName()).isEqualTo("Alex");
        assertThat(users.get(1).getName()).isEqualTo("Jochen");

    }

    @Test
    public void testTwoUserCreatedByIll() throws Exception {
        List<User> users = nameToUserFunction.apply("Jochen (für Alex krank)");
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getName()).isEqualTo("Jochen");
        assertThat(users.get(1).getName()).isEqualTo("Alex");
    }

    @Test
    public void testTwoUserCreatedByVertretung() throws Exception {
        List<User> users = nameToUserFunction.apply("Katharina J. (für Anja)");
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getName()).isEqualTo("Katharina J.");
        assertThat(users.get(1).getName()).isEqualTo("Anja");
    }
}