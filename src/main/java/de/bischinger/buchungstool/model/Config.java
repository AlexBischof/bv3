package de.bischinger.buchungstool.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.EAGER;

/**
 * Created by bischofa on 25/01/17.
 */
@Entity
public class Config {
    @Id
    @GeneratedValue
    private long id;

    public enum Type {
        SUMMER, WINTER;
    }

    @NotNull
    private Type type;

    @ElementCollection(fetch = EAGER)
    private List<String> skipValues = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<String> getSkipValues() {
        return skipValues;
    }

    public void setSkipValues(List<String> skipValues) {
        this.skipValues = skipValues;
    }
}
