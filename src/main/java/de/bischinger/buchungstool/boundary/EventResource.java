package de.bischinger.buchungstool.boundary;

import de.bischinger.buchungstool.model.Hiwi;
import de.bischinger.buchungstool.model.Warning;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import static de.bischinger.buchungstool.business.TimeNumberListFunction.getLocalTime;
import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;

/**
 * Created by bischofa on 02/02/17.
 */
@Path("/events")
@Stateless
@Produces(APPLICATION_JSON)
public class EventResource {
    @Inject
    private Logger logger;

    @Inject
    private EntityManager em;

    @GET
    public Response getEvents() {

        //TODO performance
        List<Event> events = findEvents();

        List<Warning> warnings = em.createQuery("from Warning", Warning.class).getResultList();
        events.addAll(warnings.stream().map(w -> {
            LocalDate date = w.getDate();
            return new Event(date.atTime(getLocalTime(w.getFrom())).toInstant(UTC).toEpochMilli()
                    , date.atTime(getLocalTime(w.getTo())).toInstant(UTC).toEpochMilli()
                    , w.toString().substring(11) + " [" + w.getCount()+"]")
                    .withWarningtyp(w.getTyp());
        }).collect(toList()));

        return ok(events).build();
    }

    private List<Event> findEvents() {
        List<Hiwi> hiwis = em.createQuery("from Hiwi", Hiwi.class).getResultList();

        return hiwis.stream()
                //To schedules
                .flatMap(h -> {
                    String hiwiName = h.getName();
                    return h.getScheduleMap().entrySet().stream()
                            .flatMap(s -> {
                                LocalDate date = s.getKey();

                                return s.getValue().getBookingList().stream()
                                        .map(b -> new Event(
                                                date.atTime(b.getFromAsLocalTime()).toInstant(UTC).toEpochMilli()
                                                , date.atTime(b.getToAsLocalTime()).toInstant(UTC).toEpochMilli()
                                                , hiwiName));
                            });

                }).collect(toList());
    }
}
