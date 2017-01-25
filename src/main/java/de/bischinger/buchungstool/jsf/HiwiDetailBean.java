package de.bischinger.buchungstool.jsf;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.inject.Inject;

/**
 * Created by Alex Bischof on 25.01.2017.
 */
@ManagedBean
@RequestScoped
public class HiwiDetailBean
{
	@ManagedProperty(value = "#{param.name}")
	private String name;

	@Inject private HiwiListProcucer hiwiListProcucer;

	@PostConstruct
	public void init()
	{
		if(!hiwiListProcucer.getHiwis().stream().filter(hiwi -> hiwi.getName().equals(name)).findFirst().isPresent())
		{
			this.name = "Hiwi nicht vorhanden";
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
