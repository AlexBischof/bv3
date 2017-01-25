package de.bischinger.buchungstool.boundary;

import de.bischinger.buchungstool.business.CapacityReader;
import de.bischinger.buchungstool.business.ImportBean;
import de.bischinger.buchungstool.business.importer.IcsFileReadException;
import de.bischinger.buchungstool.model.Capacity;
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
import java.util.logging.Logger;

import static java.io.File.createTempFile;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.ok;
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
    @Consumes("multipart/form-data")
    public Response uploadCal(MultipartFormDataInput input) {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("file");

        for (InputPart inputPart : inputParts) {
            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                String fileName = getFileName(inputPart.getHeaders());
                logger.info(fileName);
                File file = createTempFile("buchungstool", ".ics");
                writeFile(toByteArray(inputStream), file);
                file.deleteOnExit();

                importBean.doImport(file);
            } catch (IOException | IcsFileReadException e) {
                return Response.status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        }

        return ok().build();
    }

    @POST
    @Path("capacity")
    @Consumes("multipart/form-data")
    public Response uploadCapaciy(MultipartFormDataInput input) {
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

                CapacityReader capacityReader = new CapacityReader();
                capacityReader.read(file.toPath())
                        .forEach((k, v) -> {
                                    //Update if already exists
                                    Capacity dbCapacity = em.find(Capacity.class, k);
                                    if (dbCapacity == null) {
                                        em.persist(new Capacity(k, v));
                                    } else {
                                        dbCapacity.setNumber(v);
                                    }
                                }
                        );
            } catch (IOException | IcsFileReadException e) {
                return Response.status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        }

        return ok().build();
    }

    private void writeFile(byte[] content, File file) throws IOException {
        //String filename = file.getName();

        FileOutputStream fop = new FileOutputStream(file);
        fop.write(content);
        fop.flush();
        fop.close();
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

	/*public Response uploadFile(MultipartFormDataInput input) throws URISyntaxException {
        LOGGER.info(">>>> sit back - starting file upload...");

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("file");

		for (InputPart inputPart : inputParts) {
			try {
				InputStream inputStream = inputPart.getBody(InputStream.class, null);

				byte[] bytes = IOUtils.toByteArray(inputStream);

				String fileName = getFileName(inputPart.getHeaders());
				File file = createTempFile("buchungstool", ".ics");
				writeFile(bytes, file);
				file.deleteOnExit();

				componentEventConverter.convert(file).setFileName(fileName);
			} catch (IOException | IcsFileReadException e) {
				return Response.status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			}
		}

		Response response = Response.seeOther(new URI("/../index.html")).build();
		return response;
	}*/
}