package de.bischinger.buchungstool.business.importer;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Created by bischofa on 28/06/16.
 */
class NameNormalization implements UnaryOperator<String> {

    private static Pattern withinBrackets = Pattern.compile("\\(([^)]+)\\)");

    @Override
    public String apply(String name) {
        name = name.split(" bei")[0];

        return name.split("\\(")[0].trim();
    }
}
