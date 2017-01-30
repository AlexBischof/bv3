package de.bischinger.buchungstool.business;

import de.bischinger.buchungstool.model.SkipValue;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Created by bischofa on 25/01/17.
 */
@Stateless
public class SkipValueRepository {
    @Inject
    private EntityManager em;

    @Inject
    private Event<SkipValue> updateSkipValueEvent;

    @Inject
    private Logger logger;

    public List<SkipValue> findAllOrderedByDate() {
        return em.createQuery("from SkipValue order by skipValue", SkipValue.class).getResultList();
    }

    public Supplier<List<String>> findAllSkipValues() {
        return () -> em.createQuery("select skipValue from SkipValue", String.class).getResultList();
    }

    public void add(String skipValueString) {
        logger.info(skipValueString + " added.");
        if (isNotEmpty(skipValueString)) {
            SkipValue newSkipValue = new SkipValue(skipValueString);
            em.persist(newSkipValue);
            logger.info(skipValueString + " added.");
            updateSkipValueEvent.fire(newSkipValue);
        }
    }

    public void delete(long id) {
        SkipValue skipValue = em.find(SkipValue.class, id);
        if (skipValue != null) {
            em.remove(skipValue);
            logger.info(skipValue + " deleted.");
            updateSkipValueEvent.fire(skipValue);
        }
    }
}
