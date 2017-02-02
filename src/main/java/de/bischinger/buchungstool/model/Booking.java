package de.bischinger.buchungstool.model;

import de.bischinger.buchungstool.business.NettoDurationFunction;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static de.bischinger.buchungstool.business.TimeNumberListFunction.getLocalTime;
import static de.bischinger.buchungstool.business.TimeNumberListFunction.getNumber;
import static de.bischinger.buchungstool.model.BookingTyp.Ill;
import static java.util.Arrays.asList;
import static javax.persistence.FetchType.EAGER;
import static org.apache.commons.lang.ArrayUtils.isNotEmpty;

/**
 * Created by bischofa on 28/06/16.
 */
@Entity
public class Booking extends RootPojo
{
	private static final long serialVersionUID = 655879618662075580L;

	@Column(name = "_from")
	private int from;
	private int to;
	@ElementCollection(fetch = EAGER)
	private Set<BookingTyp> bookingTyp = new HashSet<>();
	private int bruttoDuration;
	private int nettoDuration;

	public Booking()
	{
	}

	public Booking(LocalTime from, LocalTime to, BookingTyp[] typ)
	{
		this.from = getNumber(from);
		this.to = getNumber(to);
		if(isNotEmpty(typ))
		{
			bookingTyp.addAll(asList(typ));
		}
	}

	public void calcDuration(NettoDurationFunction durationFunction)
	{
		this.bruttoDuration = (int) ((to - from) / 4d * 60);
		this.nettoDuration = durationFunction.apply(bruttoDuration);
	}

	public int getBruttoDuration()
	{
		return bruttoDuration;
	}

	public int getNettoDuration()
	{
		return nettoDuration;
	}

	public int getFrom()
	{
		return from;
	}

	public LocalTime getFromAsLocalTime(){
		return getLocalTime(from);
	}

	public LocalTime getToAsLocalTime(){
		return getLocalTime(to);
	}

	public int getTo()
	{
		return to;
	}

	public boolean isNotIll()
	{
		return bookingTyp == null || !bookingTyp.contains(Ill);
	}

	@Override
	public String toString()
	{
		return "Booking{" +
				"from=" + from +
				", to=" + to +
				", bookingTyp=" + bookingTyp +
				", bruttoDuration=" + bruttoDuration +
				", nettoDuration=" + nettoDuration +
				'}';
	}
}
