package de.bischinger.buchungstool.business;

import de.bischinger.buchungstool.model.SkipValue;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import static java.util.stream.Stream.of;

/**
 * Created by bischofa on 30/01/17.
 */
@Singleton
@Startup
public class StartupBean {
    @Inject
    private EntityManager em;

    @PostConstruct
    public void init() {
        of("teambesprechung*,zsb*,geburtstag*,schulung,sis ge*,feiertag*,*sissis".split(","))
                .map(s -> s.replaceAll("\\*", ".*")).forEach(s -> em.persist(new SkipValue(s)));

        //housekeeping: keep last 5 imports
        em.createQuery("from CalendarImport order by createdOn desc").getResultList()
                .stream()
                .skip(5)
                .forEach(file -> em.remove(file));
    }
}
