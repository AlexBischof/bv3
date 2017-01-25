package de.bischinger.buchungstool.jsf;

import de.bischinger.buchungstool.model.Warning;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Alex Bischof on 25.01.2017.
 */
@RequestScoped
public class WarningListProcucer implements Serializable
{
	private static final long serialVersionUID = 3338135449439313736L;

	private List<Warning> warnings;

	@Produces
	@Named
	public List<Warning> getWarnings()
	{
		return warnings;
	}
}
