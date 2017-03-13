package de.bischinger.buchungstool.boundary;

import de.bischinger.buchungstool.business.HiwiRepository;
import de.bischinger.buchungstool.jsf.BookingDto;
import de.bischinger.buchungstool.model.Hiwi;
import org.apache.poi.xssf.usermodel.*;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static java.net.URLDecoder.decode;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.Response.ok;
import static org.apache.poi.xssf.usermodel.XSSFCell.CELL_TYPE_STRING;

/**
 * Created by Alex Bischof on 13.02.2017.
 */
@Path("print")
public class PrintResource
{
	@Inject
	private HiwiRepository hiwiRepository;

	@Inject
	private Logger logger;

	@Path("/hiwis")
	@GET
	@Produces("application/xlsx")
	public Response printHiwis() throws UnsupportedEncodingException
	{
		List<Hiwi> hiwis = hiwiRepository.findAllOrderedByName();

		AtomicInteger rowCounter = new AtomicInteger(0);
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Gesamtübersicht Hiwis");
		XSSFRow headerRow = sheet.createRow(rowCounter.getAndIncrement());

		int colNum = 0;
		//Set header
		for (String headerColumnName : asList("Name", "Gesamt in Stunden", "Stunden pro Monat"))
		{
			XSSFCell cell = headerRow.createCell(colNum++, CELL_TYPE_STRING);
			cell.setCellValue(new XSSFRichTextString(headerColumnName));
		}

		//data
		hiwis.forEach(hiwi ->
		{
			//create row
			XSSFRow row = sheet.createRow(rowCounter.getAndIncrement());

			//create data
			row.createCell(0, CELL_TYPE_STRING).setCellValue(new XSSFRichTextString(hiwi.getName()));
			row.createCell(1, CELL_TYPE_STRING).setCellValue(hiwi.getOverallNetto());
			row.createCell(2, CELL_TYPE_STRING).setCellValue(new XSSFRichTextString(hiwi.getMonthlyNettoAsString()));
		});

		autoSizeColumns(workbook, "Gesamtübersicht Hiwis");

		StreamingOutput streamingOutput = workbook::write;

		return ok(streamingOutput).header("Content-Disposition", "attachment; filename=\"Gesamtuebersicht\".xlsx")
				.build();
	}

	@Path("{hiwi}")
	@GET
	@Produces("application/xlsx")
	public Response printHiwi(@PathParam("hiwi") String hiwiName) throws UnsupportedEncodingException
	{
		String decodedName = decode(hiwiName, "UTF-8");
		List<BookingDto> bookings = hiwiRepository.findByName(decodedName).getBookings();

		AtomicInteger rowCounter = new AtomicInteger(0);
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(decodedName);
		XSSFRow headerRow = sheet.createRow(rowCounter.getAndIncrement());

		int colNum = 0;
		//Set header
		for (String headerColumnName : asList("Datum", "Zeitraum", "Nettoarbeitszeit in Stunden", "Bruttoarbeitszeit in Stunden"))
		{
			XSSFCell cell = headerRow.createCell(colNum++, CELL_TYPE_STRING);
			cell.setCellValue(new XSSFRichTextString(headerColumnName));
		}

		//data
		bookings.forEach(bookingDto ->
		{
			//create row
			XSSFRow row = sheet.createRow(rowCounter.getAndIncrement());

			//create data
			row.createCell(0, CELL_TYPE_STRING).setCellValue(new XSSFRichTextString(bookingDto.getDate()));
			row.createCell(1, CELL_TYPE_STRING)
					.setCellValue(new XSSFRichTextString(bookingDto.getStart() + " - " + bookingDto.getEnde()));
			row.createCell(2, CELL_TYPE_STRING).setCellValue(new XSSFRichTextString(bookingDto.getNettoDuration() + ""));
			row.createCell(3, CELL_TYPE_STRING).setCellValue(new XSSFRichTextString(bookingDto.getBruttoDuration() + ""));
		});

		autoSizeColumns(workbook, decodedName);

		StreamingOutput streamingOutput = workbook::write;

		return ok(streamingOutput).header("Content-Disposition", "attachment; filename=\"" + decodedName + "\".xlsx")
				.build();
	}

	private void autoSizeColumns(XSSFWorkbook wb, String sheetName)
	{
		XSSFSheet sheet = wb.getSheet(sheetName);
		if(sheet == null)
		{
			throw new IllegalArgumentException("Sheet " + sheetName + " nicht gefunden");
		}

		int rowNum = sheet.getFirstRowNum();
		XSSFRow row = sheet.getRow(rowNum);
		for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); ++i)
		{
			sheet.autoSizeColumn(i);
		}
	}
}
