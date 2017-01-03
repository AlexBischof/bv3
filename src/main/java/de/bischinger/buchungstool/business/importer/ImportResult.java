package de.bischinger.buchungstool.business.importer;

import de.bischinger.buchungstool.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * Created by bischofa on 03/01/17.
 */
public class ImportResult {
    private List<User> users = new ArrayList<>();
    private Set<Event> events = new HashSet<>();
    private List<String> errors = new ArrayList<>();

    public void addUsers(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public Collection<Event> getEvents(Predicate<Event> predicate) {
        if (predicate == null) {
            return events;
        }

        return events.stream().filter(predicate).collect(toList());
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addEvent(Event event) {
        requireNonNull(event);
        if (!events.contains(event)) {
            events.add(event);
        }
    }

    public void addEvent(Date start, Date end, String originalName) {
        addEvent(new Event(start, end, originalName));
    }

    public void addError(User user, LocalDateTime fromDate, LocalDateTime toDate, String message) {
        this.errors.add(String.format("%s %s %s: Fehler %s", user, fromDate, toDate, message));
    }
}
