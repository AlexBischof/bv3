package de.bischinger.buchungstool.jsf;

import de.bischinger.buchungstool.business.HiwiRepository;
import de.bischinger.buchungstool.model.Hiwi;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static javax.enterprise.event.Reception.IF_EXISTS;

/**
 * Created by Alex Bischof on 25.01.2017.
 */
@SessionScoped
public class HiwiListProcucer implements Serializable
{
	private static final long serialVersionUID = -5273419818689190540L;

	@Inject
	private HiwiRepository hiwiRepository;

	private List<Hiwi> hiwis;

	@Produces
	@Named
	public List<Hiwi> getHiwis()
	{
		return hiwis;
	}

	@Produces
	@Named
	public String getHiwiNames()
	{
		return hiwis.stream()
				.map(Hiwi::getName)
				.map(n -> "'" + n + "'")
				.collect(joining(","));
	}

	public void onMemberListChanged(@Observes(notifyObserver = IF_EXISTS) final Hiwi customer)
	{
		retrieveAllHiwisOrderedByName();
	}

	@PostConstruct
	public void retrieveAllHiwisOrderedByName()
	{
		hiwis = hiwiRepository.findAllOrderedByName();
	}
}
