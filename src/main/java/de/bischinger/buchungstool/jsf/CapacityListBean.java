package de.bischinger.buchungstool.jsf;

import de.bischinger.buchungstool.business.CapacityRepository;
import de.bischinger.buchungstool.model.Capacity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static javax.enterprise.event.Reception.IF_EXISTS;

/**
 * Created by Alex Bischof on 25.01.2017.
 */
@RequestScoped
@Named
public class CapacityListBean {
    @Inject
    private CapacityRepository capacityRepository;

    private List<Capacity> capacities;

    public List<Capacity> getCapacities() {
        return capacities;
    }

    public void onCapacitiesChanged(@Observes(notifyObserver = IF_EXISTS) final Capacity capacity) {
        retrieveAllCapacities();
    }

    public void delete(Capacity capacity) {
        capacityRepository.delete(capacity.getDate());
    }

    @PostConstruct
    public void retrieveAllCapacities() {
        capacities = capacityRepository.findAllOrderedByDate();
    }
}
