package de.bischinger.buchungstool.business;

import de.bischinger.buchungstool.model.Warning;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class WarningRepository {
    @Inject
    private EntityManager em;

    public List<Warning> findAllOrderedByDate() {
        return em.createQuery("from Warning order by date", Warning.class).getResultList();
    }

    public void deleteAll() {
        findAllOrderedByDate().forEach(h -> em.remove(h));
    }
}
