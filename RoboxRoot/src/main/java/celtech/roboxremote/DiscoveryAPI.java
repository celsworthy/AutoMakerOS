package celtech.roboxremote;

import celtech.roboxbase.comms.remote.clear.ListCamerasResponse;
import celtech.roboxbase.comms.remote.clear.ListPrintersResponse;
import celtech.roboxbase.comms.remote.clear.WhoAreYouResponse;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxremote.comms.CameraCommsManager;
import celtech.roboxremote.utils.NetworkUtils;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.paint.Color;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
@Path("/discovery")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryAPI
{

    @JsonIgnore
    private static Stenographer steno = StenographerFactory.getStenographer(DiscoveryAPI.class.getName());

    private final CameraCommsManager cameraCommsManager;
    
    public DiscoveryAPI(CameraCommsManager cameraCommsManager)
    {
        this.cameraCommsManager = cameraCommsManager;
    }
    
    @RolesAllowed("root")
    @GET
    @Timed
    @Path("/listPrinters")
    @Consumes(MediaType.APPLICATION_JSON)
    public ListPrintersResponse listPrinters()
    {
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            ListPrintersResponse response = new ListPrintersResponse(PrinterRegistry.getInstance().getRemotePrinterIDs());
            steno.trace("Returning " + response.getPrinterIDs().size() + " printers");
            
            return response;
        } else
        {
            return null;
        }
    }
    
    @RolesAllowed("root")
    @GET
    @Timed
    @Path("/listCameras")
    @Consumes(MediaType.APPLICATION_JSON)
    public ListCamerasResponse listCameras(@Context HttpServletRequest request)
    {
        ListCamerasResponse response = null;
        try {
            response = new ListCamerasResponse(cameraCommsManager.getAllCameraInfo());
        } catch(Exception e) {
            steno.exception("Exception in list camera response", e);
        }
        return response;
    }

    @GET
    @Timed(name = "getFingerprint")
    @Path("/whoareyou")
    @Consumes(MediaType.APPLICATION_JSON)
    public WhoAreYouResponse getFingerprint(@Context HttpServletRequest request, @QueryParam("pc")String pc, @QueryParam("rid")String rid, @QueryParam("ru")String ru)
    {
        boolean reportUpgrading = (ru != null && ru.equalsIgnoreCase("yes"));
        Root r = Root.getInstance();
        if (!r.getIsStopping() &&
            (reportUpgrading || !r.getIsUpgrading()) &&
            PrinterRegistry.getInstance() != null)
        {
            String hostAddress = "Unknown";

            try
            {
                hostAddress = NetworkUtils.determineIPAddress();
            } catch (SocketException e)
            {
                steno.error("/whoareyou(" + request.getRemoteAddr() + "): unable to get current IP " + e.getMessage());
            }
            
            List<String> printerColours = null;
            
            // If we have printer colours requested from AutoMaker we return them 
            if(pc != null && pc.equalsIgnoreCase("yes")) 
            {    
                printerColours = new ArrayList<>();
                
                Map<String, Printer> remotePrinters = PrinterRegistry.getInstance().getRemotePrinters();
                if(remotePrinters != null && !remotePrinters.isEmpty()) 
                {
                    for(Printer printer : remotePrinters.values()) 
                    {
                        Color printerColour = printer.getPrinterIdentity().printerColourProperty().get();
                        printerColours.add(printerColour.toString());
                    }
                }
            } 
            
            String rootUUID = null;
            if (rid != null && rid.equalsIgnoreCase("yes")) 
                rootUUID = RootUUID.get();
            
            String upgradeStatus = null;
            if (reportUpgrading) 
                upgradeStatus = r.getIsUpgrading() ? "upgrading" : "";

            return new WhoAreYouResponse(PrinterRegistry.getInstance().getServerName(),
                    BaseConfiguration.getApplicationVersion(),
                    hostAddress,
                    printerColours,
                    rootUUID,
                    upgradeStatus);
        } else
        {
            return null;
        }
    }
}
