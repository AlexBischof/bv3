package de.bischinger.buchungstool.jsf;

import de.bischinger.buchungstool.model.Hiwi;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.inject.Inject;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Created by Alex Bischof on 25.01.2017.
 */
@ManagedBean
@RequestScoped
public class HiwiDetailBean
{
	@ManagedProperty(value = "#{param.name}")
	private String name;

	private Hiwi hiwi;

	@Inject
	private HiwiListProcucer hiwiListProcucer;

	@PostConstruct
	public void init()
	{
		Optional<Hiwi> hiwiOptional = hiwiListProcucer.getHiwis().stream().filter(hiwi -> hiwi.getName().equals(name))
				.findFirst();
		if(hiwiOptional.isPresent())
		{
			this.hiwi = hiwiOptional.get();
			this.name = hiwi.getName();
		}
		else
		{
			this.name = "Hiwi nicht vorhanden";
		}
	}

	public String getName()
	{
		return name;
	}

	public List<BookingDto> getBookings()
	{
		DateTimeFormatter dateTimeFormatter = ofPattern("dd.MM.yyyy");
		return hiwi.getScheduleMap().entrySet().stream()
				.sorted(comparing(Entry::getKey))
				.flatMap(e -> e.getValue().getBookingList().stream().map(b ->
						new BookingDto(dateTimeFormatter.format(e.getKey()), b.getFrom(), b.getTo(), b.getNettoDuration(),
								b.getBruttoDuration())
				))
				.collect(toList());
	}

	public void setName(String name)
	{
		this.name = name;
	}

}
