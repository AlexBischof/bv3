package de.bischinger.buchungstool.business.importer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static de.bischinger.buchungstool.business.importer.Assertions.assertThat;
import static org.junit.runners.Parameterized.Parameter;

/**
 * Created by bischofa on 28/06/16.
 */
@RunWith(Parameterized.class)
public class IcsImporterTest {

    private IcsImporter icsImporter;

    @Parameter
    public String dienstplan;

    @Parameter(1)
    public int expectedUsers;

    @Parameters
    public static Object[] data() {
        return new Object[][]{
                {"/SIS-Dienstplan1104-1507.ics", 27},
                {"/Kalender von SIS-Dienstplan_28.11.-1.1.ics", 16},
                {"/Kalender von SIS-Dienstplan_18.7.-14.10_tagesaktuell.ics", 24},
                {"/Kalender von SIS-Dienstplan_18.07.-14.10.16.ics", 24}
        };
    }


    @Before
    public void before() throws URISyntaxException {
        URL resource = this.getClass().getResource(dienstplan);
        icsImporter = new IcsImporter(new File(resource.toURI()));
    }

    @Test
    public void testImportFile() throws Exception {
        ImportResult importResult = icsImporter.importFile();
        assertThat(importResult).hasNoErrors()
                .matches(ir -> ir.getUsers().size() == expectedUsers, "Expecting " + expectedUsers);
    }
}