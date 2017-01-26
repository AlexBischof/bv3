package de.bischinger.buchungstool.business;

import de.bischinger.buchungstool.model.Capacity;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import static java.nio.file.Paths.get;
import static java.time.Month.JULY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bischofa on 30/06/16.
 */
public class CapacityReaderTest
{
	@Test
	public void test() throws URISyntaxException, IOException
	{
		CapacityReader capacityReader = new CapacityReader();

		List<Capacity> result = capacityReader.read(get(CapacityReader.class.getResource("/capacity.csv").toURI()));

		assertThat(result.stream()
				.filter(c -> c.getDate().equals(LocalDate.of(2016, JULY, 25)))
				.map(Capacity::getNumber).findFirst().get()).isEqualTo(10);
	}
}