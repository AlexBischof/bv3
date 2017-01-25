package de.bischinger.buchungstool;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Alex Bischof on 22.11.2016.
 */
public class EntityManagerProducer
{
	@Produces
	@PersistenceContext
	private EntityManager em;
}
