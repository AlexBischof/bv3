package de.bischinger.buchungstool.business.importer;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Alexander Bischof on 31.07.15.
 */
public class IcsFileReader {
  public Calendar read(File file) throws IcsFileReadException {
    try (FileInputStream fin = new FileInputStream(file)) {
      CalendarBuilder builder = new CalendarBuilder();
      return builder.build(fin);
    } catch (IOException | ParserException e) {
      throw new IcsFileReadException(e);
    }
  }
}
