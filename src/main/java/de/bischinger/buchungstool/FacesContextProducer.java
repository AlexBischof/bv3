package de.bischinger.buchungstool;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;

/**
 * Created by Alex Bischof on 22.11.2016.
 */
public class FacesContextProducer
{
	@Produces
	@RequestScoped
	public FacesContext produceFacesContext()
	{
		return FacesContext.getCurrentInstance();
	}
}
