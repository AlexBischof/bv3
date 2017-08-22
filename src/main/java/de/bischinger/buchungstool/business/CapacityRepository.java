package de.bischinger.buchungstool.business;

import de.bischinger.buchungstool.model.Capacity;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by bischofa on 25/01/17.
 */
@Stateless
public class CapacityRepository {
    @Inject
    private EntityManager em;

    @Inject
    private Event<Capacity> updateCapacityEvent;

    @Inject
    private Logger logger;

    public List<Capacity> findAllOrderedByDate() {
        return em.createQuery("from Capacity order by id", Capacity.class).getResultList();
    }

    public void add(Capacity capacity) {
        //Update if already exists
        Capacity dbCapacity = em.find(Capacity.class, capacity.getDate());
        if (dbCapacity == null) {
            em.persist(capacity);
        } else {
            dbCapacity.setNumber(capacity.getNumber());
        }
        updateCapacityEvent.fire(capacity);
    }

    public void delete(LocalDate localDate) {
        Capacity capacity = em.find(Capacity.class, localDate);
        if (capacity != null) {
            em.remove(capacity);
            logger.info(capacity + " deleted.");
            updateCapacityEvent.fire(capacity);
        }
    }
}
