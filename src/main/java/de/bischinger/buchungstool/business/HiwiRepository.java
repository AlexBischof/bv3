package de.bischinger.buchungstool.business;

import de.bischinger.buchungstool.model.Hiwi;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class HiwiRepository
{
	@Inject
	private EntityManager em;

	public Hiwi findById(Long id)
	{
		return em.find(Hiwi.class, id);
	}

	public List<Hiwi> findAllOrderedByName()
	{
		return em.createQuery("from Hiwi order by name", Hiwi.class).getResultList();
	}

	public void deleteAll()
	{
		findAllOrderedByName().forEach(h -> em.remove(h));
	}
}
