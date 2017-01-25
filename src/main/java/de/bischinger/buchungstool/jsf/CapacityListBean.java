package de.bischinger.buchungstool.jsf;

import de.bischinger.buchungstool.model.Capacity;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by Alex Bischof on 25.01.2017.
 */
@RequestScoped
public class CapacityListBean
{
	private static final long serialVersionUID = 3338135449439313736L;

	@Inject
	private EntityManager em;

	@Produces
	@Named
	public List<Capacity> getCapacities()
	{
		return em.createQuery("from Capacity order by id", Capacity.class).getResultList();
	}
}
