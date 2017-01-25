package de.bischinger.buchungstool.business;

import de.bischinger.buchungstool.business.importer.IcsImporter;
import de.bischinger.buchungstool.business.importer.ImportResult;
import de.bischinger.buchungstool.model.Hiwi;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.File;
import java.util.logging.Logger;

/**
 * Created by Alex Bischof on 25.01.2017.
 */
@Stateless
public class ImportBean
{
	@Inject private Logger logger;

	@Inject
	private Event<Hiwi> hiwiEventSrc;
	@Inject
	private HiwiRepository hiwiRepository;
	@Inject
	private EntityManager em;

	public void doImport(File file)
	{
		//FIXME delete all Hiwis
		hiwiRepository.deleteAll();

		IcsImporter icsImporter = new IcsImporter(file);
		ImportResult importResult = icsImporter.importFile();
		importResult.getHiwis().forEach(hiwi -> em.persist(hiwi));
		hiwiEventSrc.fire(new Hiwi());
		logger.info("" + importResult);
	}
}
