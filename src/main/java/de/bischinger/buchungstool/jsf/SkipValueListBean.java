package de.bischinger.buchungstool.jsf;

import de.bischinger.buchungstool.business.SkipValueRepository;
import de.bischinger.buchungstool.model.SkipValue;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

import static javax.enterprise.event.Reception.IF_EXISTS;

/**
 * Created by Alex Bischof on 25.01.2017.
 */
@RequestScoped
@Named
public class SkipValueListBean implements Serializable {
    private static final long serialVersionUID = -5273419818689190540L;

    @Inject
    private SkipValueRepository skipValueRepository;

    private List<SkipValue> skipValues;

    private String newSkipValue;

    public List<SkipValue> getSkipValues() {
        return skipValues;
    }

    public void onSkipValuesChanged(@Observes(notifyObserver = IF_EXISTS) final SkipValue skipValue) {
        retrieveAllWarnings();
    }

    public String getNewSkipValue() {
        return newSkipValue;
    }

    public void setNewSkipValue(String newSkipValue) {
        this.newSkipValue = newSkipValue;
    }

    public String add() {
        skipValueRepository.add(newSkipValue);
        return "config?faces-redirect=true";
    }

    public void delete(SkipValue skipValue) {
        skipValueRepository.delete(skipValue.getId());
    }

    @PostConstruct
    public void retrieveAllWarnings() {
        newSkipValue = null;
        skipValues = skipValueRepository.findAllOrderedByDate();
    }
}
