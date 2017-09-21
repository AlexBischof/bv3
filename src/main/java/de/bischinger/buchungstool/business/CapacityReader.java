package de.bischinger.buchungstool.business;

import de.bischinger.buchungstool.model.Capacity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

import static java.lang.Character.isDigit;
import static java.lang.Integer.valueOf;
import static java.nio.file.Files.readAllLines;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;
import static java.util.Collections.emptyList;
import static java.util.logging.Logger.getLogger;
import static java.util.stream.Collectors.toList;

/**
 * Created by bischofa on 30/06/16.
 */
public class CapacityReader {

    private static DateTimeFormatter dateTimeFormatter = ofPattern("dd.MM.yyyy");
    private static final SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd.MM.yyyy");

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

            //Sucht letztes Sheet, dass mit einer Zahl beginnt
            XSSFSheet sheet = null;
            Iterator<XSSFSheet> sheetIterator = (Iterator<XSSFSheet>) (Iterator<? extends Sheet>) workbook.iterator();
            while (sheetIterator.hasNext()) {
                XSSFSheet next = sheetIterator.next();
                String sheetName = next.getSheetName();
                if (isDigit(sheetName.charAt(0))) {
                    sheet = next;
                }
            }

            Logger.getLogger("readXls").info("sheetname: " + sheet.getSheetName());
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    Cell cell = row.getCell(4);
                    if (cell == null) {
                        continue;
                    }
                    CellValue evaluate = evaluator.evaluate(cell);
                    if (evaluate == null) {
                        continue;
                    }
                    double capacity = evaluate.getNumberValue();
                    if (capacity > 0) {
                        //Try DateField with dd.MM.yyyy
                        try {
                            Date dateCellValue = row.getCell(0).getDateCellValue();
                            if (dateCellValue != null) {
                                LocalDate from = parse(DD_MM_YYYY.format(dateCellValue), dateTimeFormatter);
                                map.put(from, (int) capacity);
                            }
                        } catch (IllegalStateException e) {
                            //try string stuff
                            String dateAsString = row.getCell(0).getStringCellValue();
                            //Takes either DD.MM. or DD.MM.YYYY
                            String obj = dateAsString + (dateAsString.length() == 6 ? getInstance().get(YEAR) : "");
                            map.put(parse(obj, dateTimeFormatter), (int) capacity);
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    //skip on purpose
                }
            }

            List<Capacity> collect = map.entrySet().stream().map(e -> new Capacity(e.getKey(), e.getValue()))
                    .collect(toList());
            return collect;
        } catch (IOException e) {
            e.printStackTrace();
            return emptyList();
        }
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(new SimpleDateFormat("dd.MM").parse("31.07"));
    }
}
