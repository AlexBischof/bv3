package de.bischinger.buchungstool.business;

import de.bischinger.buchungstool.model.Capacity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

import static java.lang.Integer.valueOf;
import static java.nio.file.Files.readAllLines;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Collections.emptyList;
import static java.util.logging.Logger.getLogger;
import static java.util.stream.Collectors.toList;

/**
 * Created by bischofa on 30/06/16.
 */
public class CapacityReader {

    private static DateTimeFormatter dateTimeFormatter = ofPattern("dd.MM.yyyy");

    public static List<Capacity> readCsv(Path path) {

        getLogger(CapacityReader.class.getName()).info("readCsv");

        Map<LocalDate, Integer> map = new HashMap<>();
        try {
            readAllLines(path).stream()
                    .filter(s -> !s.trim().isEmpty())
                    .map(s -> s.split(",")).forEach(s ->
            {
                LocalDate date = parse(s[0].trim(), dateTimeFormatter);
                Integer value = valueOf(s[1].trim());
                map.put(date, value);
            });
            return map.entrySet().stream().map(e -> new Capacity(e.getKey(), e.getValue()))
                    .collect(toList());
        } catch (IOException e) {
            e.printStackTrace();
            return emptyList();
        }
    }

    public static List<Capacity> readXls(Path path) {
        Map<LocalDate, Integer> map = new HashMap<>();

        getLogger(CapacityReader.class.getName()).info("readXls");

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(path.toFile()));

            XSSFSheet sheet = workbook.getSheetAt(workbook.getNumberOfSheets() - 1);
            Logger.getLogger("readXls").info("sheetname: " + sheet.getSheetName());
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Iterator<Row> rowIterator = sheet.rowIterator();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    Cell cell = row.getCell(5);
                    CellValue evaluate = evaluator.evaluate(cell);
                    if (evaluate == null){
                        continue;
                    }
                    double capacity = evaluate.getNumberValue();
                    if (capacity > 0) {
                        Date dateCellValue = row.getCell(1).getDateCellValue();
                        if (dateCellValue != null) {
                            LocalDate from = parse(simpleDateFormat.format(dateCellValue), dateTimeFormatter);
                            map.put(from, (int) capacity);
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    //skip on purpose
                }
            }

            List<Capacity> collect = map.entrySet().stream().map(e -> new Capacity(e.getKey(), e.getValue()))
                    .collect(toList());
            System.out.println(collect);
            return collect;
        } catch (IOException e) {
            e.printStackTrace();
            return emptyList();
        }
    }
}
