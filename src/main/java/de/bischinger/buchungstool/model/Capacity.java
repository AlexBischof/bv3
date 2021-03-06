package de.bischinger.buchungstool.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Alex Bischof on 25.01.2017.
 */
@Entity
public class Capacity
{
	private final static DateTimeFormatter df =  DateTimeFormatter.ofPattern("dd.MM.yyyy");

	@Id
	private LocalDate date;
	@NotNull
	private int number;

	@Transient
	private String formattedDate;

	public Capacity()
	{
	}

	public Capacity(LocalDate date, int number)
	{
		this.date = date;
		this.number = number;
	}

	public LocalDate getDate()
	{
		return date;
	}

	public String getFormattedDate()
	{
		if(formattedDate == null)
		{
			formattedDate = date.format(df);
		}
		return formattedDate;
	}

	public void setDate(LocalDate date)
	{
		this.date = date;
	}

	public int getNumber()
	{
		return number;
	}

	public void setNumber(int number)
	{
		this.number = number;
	}

	@Override public String toString()
	{
		return "Capacity{" +
				"date=" + date +
				", number=" + number +
				'}';
	}
}
