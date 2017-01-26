package de.bischinger.buchungstool.business.validation;

import de.bischinger.buchungstool.business.CapacityReader;
import de.bischinger.buchungstool.business.importer.IcsImporter;
import de.bischinger.buchungstool.model.Capacity;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static java.nio.file.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bischofa on 28/06/16.
 */
public class ValidationServiceTest
{
	private IcsImporter icsImporter;
	private ValidationService validationService;

	@Before
	public void before() throws URISyntaxException
	{
		URL resource = this.getClass().getResource("/Kalender von SIS-Dienstplan_18.07.-14.10.16.ics");
		icsImporter = new IcsImporter(new File(resource.toURI()));

		List<Capacity> capacityList = null;
		try
		{
			CapacityReader capacityReader = new CapacityReader();
			capacityList = capacityReader.read(get(CapacityReader.class.getResource("/capacity.csv").toURI()));
		}
		catch(IOException | URISyntaxException e)
		{
			e.printStackTrace();
		}

		validationService = new ValidationService(capacityList, 9, 5);
	}

	@Test
	public void test()
	{
		assertThat(validationService.validate(icsImporter.importFile().getHiwis().parallelStream())).isNotEmpty();
	}
}