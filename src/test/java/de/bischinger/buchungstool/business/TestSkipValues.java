package de.bischinger.buchungstool.business;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;

/**
 * Created by bischofa on 30/01/17.
 */
public class TestSkipValues {
    public static List<String> testSkipValues() {
        return of("teambesprechung*,zsb*,geburtstag*,schulung,sis ge*,feiertag*,*sissis".split(","))
                .map(s -> s.replaceAll("\\*", ".*")).collect(toList());

    }

}
