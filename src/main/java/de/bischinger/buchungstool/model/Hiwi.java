package de.bischinger.buchungstool.model;

import de.bischinger.buchungstool.business.NettoDurationFunction;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

/**
 * Created by bischofa on 28/06/16.
 */
@Entity
public class Hiwi extends RootPojo
{
	private static final long serialVersionUID = -7130519343755801047L;

	private String name;
	private String originalName;

	@OneToMany(cascade = { PERSIST, REMOVE }, fetch = EAGER, orphanRemoval = true)
	private Map<LocalDate, Schedule> scheduleMap;

	public Hiwi()
	{
	}

	public Hiwi(String name, String originalName)
	{
		this.name = name;
		this.originalName = originalName;
	}

	public String getName()
	{
		return name;
	}

	public String getOriginalName()
	{
		return originalName;
	}

	public void addHiwi(Hiwi other)
	{
		other.getScheduleMap().forEach((otherLocalDate, otherSchedule) ->
		{
			Schedule curSchedule = getScheduleMap().get(otherLocalDate);
			if(curSchedule == null)
			{
				getScheduleMap().put(otherLocalDate, otherSchedule);
			}
			else
			{
				curSchedule.addBookingList(otherSchedule.getBookingList());
			}
		});
	}

	public Map<LocalDate, Schedule> getScheduleMap()
	{
		if(scheduleMap == null)
		{
			scheduleMap = new HashMap<>();
		}
		return scheduleMap;
	}

	@Override
	public String toString()
	{
		return "Hiwi{" +
				"name='" + name + '\'' +
				", originalName='" + originalName + '\'' +
				", scheduleMap=" + scheduleMap +
				'}';
	}

	public void addTimes(LocalDateTime fromDate, LocalDateTime toDate, BookingTyp[] bookingTyps)
	{
		LocalDate localDate = fromDate.toLocalDate();
		LocalTime fromTime = fromDate.toLocalTime();
		LocalTime toTime = toDate.toLocalTime();

		Schedule schedule = getScheduleMap().computeIfAbsent(localDate, k -> new Schedule());
		schedule.addBooking(fromTime, toTime, bookingTyps);
	}

	public void calcDurations(NettoDurationFunction durationFunction)
	{
		getScheduleMap().values().forEach(schedule -> schedule.getBookingList().forEach(booking ->
		{
			booking.calcDuration(durationFunction);
		}));
	}
}
