package de.bischinger.buchungstool.business;

import de.bischinger.buchungstool.model.Capacity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import static de.bischinger.buchungstool.business.CapacityReader.readXls;
import static java.nio.file.Paths.get;
import static java.time.LocalDate.of;
import static java.time.Month.AUGUST;
import static java.time.Month.MARCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

/**
 * Created by bischofa on 30/06/16.
 */
@RunWith(Parameterized.class)
public class CapacityXlsxReaderTest {
    @Parameters(name = "{0}")
    public static Object[][] data() {
        return new Object[][]{
                {"/Hiwis_zusätzliche Arbeitsplätze 2017-1.xlsx", of(2017, MARCH, 1), 6},
                {"/Hiwis_zusätzliche Arbeitsplätze 2017-2.xlsx", of(2017, AUGUST, 7), 14},
        };
    }

    @Parameter
    public String file;

    @Parameter(1)
    public LocalDate date;

    @Parameter(2)
    public int expectedCapacity;

    @Test
    public void testXlsx() throws URISyntaxException, IOException {
        List<Capacity> result = readXls(get(CapacityReader.class.getResource(file).toURI()));

        assertThat(result).isNotEmpty();
        assertThat(result.stream()
                .filter(c -> c.getDate().equals(date))
                .findAny().get().getNumber())
                .isEqualTo(expectedCapacity);
    }
}