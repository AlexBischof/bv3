package de.bischinger.buchungstool.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Created by bischofa on 25/01/17.
 */
@Entity
public class SkipValue extends RootPojo{
    @NotNull
    @Column(nullable = false)
    private String skipValue;

    public SkipValue() {
    }

    public SkipValue(String skipValue) {
        this.skipValue = skipValue;
    }

    public String getSkipValue() {
        return skipValue;
    }

    public void setSkipValue(String skipValue) {
        this.skipValue = skipValue;
    }
}
