package de.bischinger.buchungstool.business;

import de.bischinger.buchungstool.model.Capacity;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.valueOf;
import static java.nio.file.Files.readAllLines;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.toList;

/**
 * Created by bischofa on 30/06/16.
 */
public class CapacityReader
{
	public List<Capacity> read(Path path) throws IOException
	{
		Map<LocalDate, Integer> map = new HashMap<>();

		DateTimeFormatter dateTimeFormatter = ofPattern("dd.MM.yyyy");
		readAllLines(path).stream().map(s -> s.split(",")).forEach(s ->
		{
			LocalDate date = parse(s[0], dateTimeFormatter);
			Integer value = valueOf(s[1]);
			map.put(date, value);
		});

		return map.entrySet().stream().map(e -> new Capacity(e.getKey(), e.getValue()))
				.collect(toList());
	}
}
