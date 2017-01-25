package de.bischinger.buchungstool.business;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.valueOf;
import static java.nio.file.Files.readAllLines;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Created by bischofa on 30/06/16.
 */
public class CapacityReader
{
    public Map<LocalDate, Integer> read(Path path) throws IOException {
        Map<LocalDate, Integer> map = new HashMap<>();

        DateTimeFormatter dateTimeFormatter = ofPattern("dd.MM.yyyy");
        readAllLines(path).stream().map(s -> s.split(",")).forEach(s -> {
            LocalDate date = parse(s[0], dateTimeFormatter);
            Integer value = valueOf(s[1]);
            map.put(date, value);
        });

        return map;
    }
}
