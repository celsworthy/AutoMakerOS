package celtech.roboxremote;

import celtech.roboxbase.comms.remote.Configuration;
import celtech.roboxbase.printerControl.model.PrinterException;
import com.codahale.metrics.annotation.Timed;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author Ian
 */
@RolesAllowed("root")
@Path(Configuration.adminAPIService)
@Produces(MediaType.APPLICATION_JSON)
public class RootWebPageResource
{

    private final Stenographer steno = StenographerFactory.getStenographer(RootWebPageResource.class.getName());
    private final Utils utils = new Utils();

    public RootWebPageResource()
    {
    }

    @POST
    @Timed
    @Path(Configuration.shutdown)
    public void shutdown()
    {
        new Runnable()
        {
            @Override
            public void run()
            {
                steno.info("Running shutdown thread");
                Root.getInstance().stop();
                steno.info("Shutdown thread finished");
            }
        }.run();
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/setServerName")
    public Response setServerName(String serverName)
    {
        PrinterRegistry.getInstance().setServerName(serverName);
        // If this server is connected to just one RBX10 printer, then
        // assume it is a RoboxPro. Name the printer to be the same as the
        // server.
        if (PrinterRegistry.getInstance().getRemotePrinters().size() == 1)
        {
            PrinterRegistry.getInstance().getRemotePrinters().forEach((k,v) ->
                {
                    try
                    {
                        if(v.printerConfigurationProperty().get().getTypeCode().equals("RBX10"))
                        {
                            v.updatePrinterName(serverName);
                        }
                    }
                    catch (PrinterException ex)
                    {
                        steno.error("Failed to set associated Robox Pro name.");
                    }
                });
        }
           
        return Response.ok().build();
     }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("updateSystem")
    public Response updateSystem(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException
    {
        String uploadedFileLocation = System.getProperty("java.io.tmpdir") + fileDetail.getFileName();
        steno.info("Upgrade file " + uploadedFileLocation + " has been uploaded");
        // save it
        utils.writeToFile(uploadedInputStream, uploadedFileLocation);
        Root.getInstance().stop();
        return Response.ok().build();
    }
}
