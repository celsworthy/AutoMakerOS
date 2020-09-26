package celtech.roboxremote;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.comms.remote.Configuration;
import celtech.roboxbase.comms.remote.clear.WifiStatusResponse;
import celtech.roboxbase.comms.remote.types.SerializableFilament;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.MachineType;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.printerControl.model.Printer;
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
@Path(Configuration.adminAPIService)
@Produces(MediaType.APPLICATION_JSON)
public class AdminAPI
{

    private final Stenographer steno = StenographerFactory.getStenographer(AdminAPI.class.getName());
    private final Utils utils = new Utils();

    public AdminAPI()
    {
    }

    @RolesAllowed("root")
    @POST
    @Timed
    @Path(Configuration.shutdown)
    public Response shutdown()
    {
        steno.info("Shutdown requested");
        if (!Root.getInstance().getIsStopping()) {
            Root.getInstance().setIsStopping(true);
            BaseLookup.getTaskExecutor().runDelayedOnBackgroundThread(() -> Root.getInstance().stop(), 10000);
        }
        return Response.ok().build();
    }

    @RolesAllowed("root")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/setServerName")
    public Response setServerName(String serverName)
    {
        if (Root.isResponding()) {
            PrinterRegistry.getInstance().setServerName(Utils.cleanInboundJSONString(serverName));
            return Response.ok().build();
        }
        else
            return Response.serverError().status(503).build();
    }

    @RolesAllowed("root")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("updateSystem")
    public Response updateSystem(
            @FormDataParam("name") InputStream uploadedInputStream,
            @FormDataParam("name") FormDataContentDisposition fileDetail) throws IOException
    {
        Response response = null;
        if (Root.isResponding()) {
            try
            {
                String fileName = fileDetail.getFileName();
                steno.info("Asked to upgrade using file " + fileName);

                Root.getInstance().setIsUpgrading(true);
                long t1 = System.currentTimeMillis();
                String uploadedFileLocation;
                if (BaseConfiguration.getMachineType() != MachineType.WINDOWS)
                {
                    uploadedFileLocation = "/tmp/" + fileName;
                } else
                {
                    uploadedFileLocation = BaseConfiguration.getUserTempDirectory() + fileName;
                }

                // save it
                utils.writeToFile(uploadedInputStream, uploadedFileLocation);

                long t2 = System.currentTimeMillis();
                steno.info("Upgrade file " + uploadedFileLocation + " has been uploaded in " + Long.toString(t2 - t1) + "ms");

                // Shut down - but delay by 10 seconds to allow the response to go back to the requester first.
                Root.getInstance().setIsStopping(true);
                BaseLookup.getTaskExecutor().runDelayedOnBackgroundThread(() -> Root.getInstance().restart(), 10000);
                response = Response.ok().build();
            }
            catch (IOException ex){
                Root.getInstance().setIsUpgrading(false);
                response = Response.serverError().build();
            }
        }
        else
            response = Response.serverError().status(503).build();
        
        return response;
    }

    @RolesAllowed("root")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/updatePIN")
    public Response updatePIN(String newPIN)
    {
        if (Root.isResponding()) {
            Root.getInstance().setApplicationPIN(Utils.cleanInboundJSONString(newPIN));
            return Response.ok().build();
        }
        else
            return Response.serverError().status(503).build();
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/resetPIN")
    public Response resetPIN(String printerSerial)
    {
        if (Root.isResponding()) {
            boolean serialMatches = false;

            String serialToUse = Utils.cleanInboundJSONString(printerSerial);
            if (serialToUse != null)
            {
                for (Printer printer : PrinterRegistry.getInstance().getRemotePrinters().values())
                {
                    if (printer.getPrinterIdentity().printerUniqueIDProperty().get().toLowerCase().endsWith(serialToUse.toLowerCase()))
                    {
                        serialMatches = true;
                        break;
                    }
                }
            }

            if (serialMatches)
            {
                Root.getInstance().resetApplicationPINToDefault();
                return Response.ok().build();
            } else
            {
                return Response.serverError().build();
            }
        }
        else
            return Response.serverError().status(503).build();
    }

    @RolesAllowed("root")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/setWiFiCredentials")
    public Response setWiFiCredentials(String ssidAndPassword)
    {
        if (Root.isResponding()) {
            steno.info("Asked to change wifi creds to " + ssidAndPassword);
            if (WifiControl.setupWiFiCredentials(Utils.cleanInboundJSONString(ssidAndPassword)))
                return Response.ok().build();
            else
                return Response.serverError().build();
        }
        else
            return Response.serverError().status(503).build();
    }

    @RolesAllowed("root")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/enableDisableWifi")
    public Response enableDisableWifi(boolean enableWifi)
    {
        if (Root.isResponding()) {
            if (WifiControl.enableWifi(enableWifi))
                return Response.ok().build();
            else
                return Response.serverError().build();
        }
        else
            return Response.serverError().status(503).build();
    }

    @RolesAllowed("root")
    @POST
    @Timed
    @Path("/getCurrentWifiState")
    public WifiStatusResponse getCurrentWifiSSID()
    {
        if (Root.isResponding())
            return WifiControl.getCurrentWifiState();
        else
            return null;
    }

    @RolesAllowed("root")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveFilament")
    public Response saveFilament(SerializableFilament serializableFilament)
    {
        if (Root.isResponding()) {
            Filament filament = serializableFilament.getFilament();
            FilamentContainer.getInstance().saveFilament(filament);
            return Response.ok().build();
        }
        else
            return Response.serverError().status(503).build();
    }

    @RolesAllowed("root")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteFilament")
    public Response deleteFilament(SerializableFilament serializableFilament)
    {
        if (Root.isResponding()) {
            Filament filament = serializableFilament.getFilament();
            FilamentContainer.getInstance().deleteFilament(filament);
            return Response.ok().build();
        }
        else
            return Response.serverError().status(503).build();
    }

    @RolesAllowed("root")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/setUpgradeState")
    public Response setUpgradeState(boolean isUpgrading)
    {
        if (!Root.getInstance().getIsStopping()) {
            Root.getInstance().setIsUpgrading(isUpgrading);
            return Response.ok().build();
        }
        else
            return Response.serverError().status(503).build();
    }
}
