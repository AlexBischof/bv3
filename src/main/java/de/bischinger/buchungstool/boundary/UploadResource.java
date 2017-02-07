package de.bischinger.buchungstool.boundary;

import de.bischinger.buchungstool.business.ImportBean;
import de.bischinger.buchungstool.business.importer.IcsFileReadException;
import de.bischinger.buchungstool.model.Capacity;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import static de.bischinger.buchungstool.business.CapacityReader.readCsv;
import static de.bischinger.buchungstool.business.CapacityReader.readXls;
import static java.io.File.createTempFile;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static org.apache.commons.io.IOUtils.toByteArray;

/**
 * Created by Alex Bischof on 25.01.2017.
 */
@Path("/upload")
@Stateless
public class UploadResource {
    @Inject
    private Logger logger;
    @Inject
    private ImportBean importBean;
    @Inject
    private EntityManager em;

    @POST
    @Path("cal")
    @Consumes(MULTIPART_FORM_DATA)
    public Response uploadCal(@MultipartForm CalendarUploadData calendarUploadData) {
        try {
            File file = createTempFile("buchungstool", ".ics");
            writeFile(calendarUploadData.getFile(), file);
            file.deleteOnExit();

            boolean isSommer = "Sommer".equals(calendarUploadData.getPausencalculation());

            importBean.doImport(file, isSommer);
        } catch (IOException | IcsFileReadException e) {
            return status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        return ok().build();
    }

    @POST
    @Path("capacity")
    @Consumes(MULTIPART_FORM_DATA)
    public Response uploadCapacity(MultipartFormDataInput input) {

        em.createQuery("delete from Capacity").executeUpdate();

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("file");

        for (InputPart inputPart : inputParts) {
            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                String fileName = getFileName(inputPart.getHeaders());
                logger.info(fileName);
                File file = createTempFile("capacityFile", ".ics");
                writeFile(toByteArray(inputStream), file);
                file.deleteOnExit();

                BiFunction<String, java.nio.file.Path, List<Capacity>> capacitiesFunction = (f, p) -> f.endsWith("csv") ? readCsv(p) : readXls(p);
                capacitiesFunction.apply(fileName, file.toPath())
                        .forEach(capacity ->
                                {
                                    //Update if already exists
                                    Capacity dbCapacity = em.find(Capacity.class, capacity.getDate());
                                    if (dbCapacity == null) {
                                        em.persist(capacity);
                                    } else {
                                        dbCapacity.setNumber(capacity.getNumber());
                                    }
                                }
                        );
            } catch (IOException | IcsFileReadException e) {
                e.printStackTrace();
                return status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        }

        return ok().build();
    }

    private void writeFile(byte[] content, File file) throws IOException {
        try (FileOutputStream fop = new FileOutputStream(file)) {
            fop.write(content);
            fop.flush();
            fop.close();
        }
    }

    private String getFileName(MultivaluedMap<String, String> headers) {
        String[] contentDisposition = headers.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                return sanitizeFilename(filename.split("=")[1]);
            }
        }
        throw new IcsFileReadException("file not found");
    }

    private String sanitizeFilename(String s) {
        return s.trim().replaceAll("\"", "");
    }
}
