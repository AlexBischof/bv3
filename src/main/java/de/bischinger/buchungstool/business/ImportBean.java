package de.bischinger.buchungstool.business;

import de.bischinger.buchungstool.business.importer.IcsImporter;
import de.bischinger.buchungstool.business.importer.ImportResult;
import de.bischinger.buchungstool.business.validation.ValidationService;
import de.bischinger.buchungstool.model.Capacity;
import de.bischinger.buchungstool.model.Hiwi;
import de.bischinger.buchungstool.model.Warning;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Alex Bischof on 25.01.2017.
 */
@Stateless
public class ImportBean {
    @Inject
    private Logger logger;

    @Inject
    private Event<Hiwi> updateHiwisEvent;
    @Inject
    private Event<Warning> updateWarningsEvent;
    @Inject
    private HiwiRepository hiwiRepository;
    @Inject
    private WarningRepository warningRepository;
    @Inject
    private EntityManager em;

    public void doImport(File file, boolean isSommer) {
        //FIXME delete all Hiwis
        hiwiRepository.deleteAll();
        warningRepository.deleteAll();

        IcsImporter icsImporter = new IcsImporter(file, isSommer);
        ImportResult importResult = icsImporter.importFile();
        List<Hiwi> hiwis = importResult.getHiwis();
        hiwis.forEach(hiwi -> em.persist(hiwi));
        updateHiwisEvent.fire(new Hiwi());
        logger.info("" + importResult);

        ValidationService validationService = new ValidationService(
                em.createQuery("from Capacity", Capacity.class).getResultList(), 9, 5);
        //config().getInteger("buchungstool.defaultBelegung", 9),
        //FIXME config().getInteger("buchungstool.minBelegung", 5)
        validationService.validate(hiwis.stream()).forEach(warning -> em.persist(warning));
        updateWarningsEvent.fire(new Warning());
    }
}
