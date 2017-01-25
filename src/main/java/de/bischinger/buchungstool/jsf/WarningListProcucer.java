package de.bischinger.buchungstool.jsf;

import de.bischinger.buchungstool.business.WarningRepository;
import de.bischinger.buchungstool.model.Warning;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static javax.enterprise.event.Reception.IF_EXISTS;

/**
 * Created by Alex Bischof on 25.01.2017.
 */
@SessionScoped
public class WarningListProcucer implements Serializable {
    private static final long serialVersionUID = -5273419818689190540L;

    @Inject
    private WarningRepository warningRepository;

    private List<WarningDto> warnings;

    @Produces
    @Named
    public List<WarningDto> getWarnings() {
        return warnings;
    }

    public void onMemberListChanged(@Observes(notifyObserver = IF_EXISTS) final Warning warning) {
        retrieveAllWarnings();
    }

    @PostConstruct
    public void retrieveAllWarnings() {
        warnings = warningRepository.findAllOrderedByDate().stream()
                .map(w -> new WarningDto(w.getFrom(), w.getTo(), w.getTyp(), w.getCapacity(), w.getCount()))
                .collect(toList());
    }
}
