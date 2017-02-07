package de.bischinger.buchungstool.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDate;

import static de.bischinger.buchungstool.business.TimeNumberListFunction.getLocalTime;

/**
 * Created by bischofa on 28/06/16.
 */
@Entity
public class Warning extends RootPojo
{
	private static final long serialVersionUID = -5587388863099323920L;

	private LocalDate date;
	@Column(name = "_from")
	private int from;
	private int to;
	private Typ typ;
	private int capacity;
	private int count;
	private int diff;

	public enum Typ
	{
		Min, Max
	}

	public Warning()
	{
	}

	//TODO Idee dto loswerden
	public Warning(LocalDate date, int from, int to, Typ typ, int capacity, int count, int diff)
	{
		this.date = date;
		this.from = from;
		this.to = to;
		this.typ = typ;
		this.capacity = capacity;
		this.count = count;
		this.diff = diff;
	}

	public LocalDate getDate()
	{
		return date;
	}

	public int getFrom()
	{
		return from;
	}

	public int getTo()
	{
		return to;
	}

	public void setTo(int to)
	{
		this.to = to;
	}

	public Typ getTyp()
	{
		return typ;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public int getDiff()
	{
		return diff;
	}

	public void setDiff(int diff)
	{
		this.diff = diff;
	}

	public int getCapacity()
	{
		return capacity;
	}

	public void setCapacity(int capacity)
	{
		this.capacity = capacity;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;

		Warning warning = (Warning) o;

		if(from != warning.from)
			return false;
		if(to != warning.to)
			return false;
		if(diff != warning.diff)
			return false;
		if(!date.equals(warning.date))
			return false;
		return typ == warning.typ;

	}

	@Override
	public int hashCode()
	{
		int result = date.hashCode();
		result = 31 * result + from;
		result = 31 * result + to;
		result = 31 * result + typ.hashCode();
		result = 31 * result + diff;
		return result;
	}

	@Override
	public String toString() {
		return date + ": " + (typ.equals(Typ.Max) ? "Überbuchung" : "Unterbelegt") + " (" + capacity + ") "
				+ getLocalTime(from) + " - " + getLocalTime(to + 1);
	}
}
