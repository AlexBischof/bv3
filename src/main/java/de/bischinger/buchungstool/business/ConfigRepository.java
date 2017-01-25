package de.bischinger.buchungstool.business;

import de.bischinger.buchungstool.model.Config;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Created by bischofa on 25/01/17.
 */
@Stateless
public class ConfigRepository {
    @Inject
    private EntityManager em;

    public Config getConfig() {
        return em.createQuery("from Config", Config.class).getSingleResult();
    }

    public void save(Config config) {
        em.merge(config);
    }
}
