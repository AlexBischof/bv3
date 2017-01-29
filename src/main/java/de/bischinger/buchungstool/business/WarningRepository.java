package de.bischinger.buchungstool.business;

import de.bischinger.buchungstool.model.Warning;

import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class WarningRepository {
    @Inject
    private EntityManager em;

    @Inject
    private Event<Warning> updateWarningsEvent;

    @Inject
    private Logger logger;

    public List<Warning> findAllOrderedByDate() {
        return em.createQuery("from Warning order by date", Warning.class).getResultList();
    }

    public void deleteAll() {
        findAllOrderedByDate().forEach(h -> em.remove(h));
    }

    public void delete(long id) {
        Warning warning = em.find(Warning.class, id);
        if (warning != null) {
            em.remove(warning);
            logger.info(warning + " deleted.");
            updateWarningsEvent.fire(warning);
        }
    }
}
