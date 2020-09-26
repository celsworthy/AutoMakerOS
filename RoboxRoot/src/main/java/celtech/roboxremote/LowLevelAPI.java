package celtech.roboxremote;

import celtech.roboxbase.comms.remote.Configuration;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import celtech.roboxbase.comms.tx.RoboxTxPacket;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.rx.AckResponse;
import celtech.roboxbase.comms.rx.RoboxRxPacketFactory;
import celtech.roboxbase.comms.rx.RxPacketTypeEnum;
import celtech.roboxbase.comms.tx.ReadSendFileReport;
import celtech.roboxbase.comms.tx.ReportErrors;
import celtech.roboxbase.comms.tx.SendDataFileChunk;
import celtech.roboxbase.comms.tx.SendDataFileEnd;
import celtech.roboxbase.comms.tx.SendDataFileStart;
import celtech.roboxbase.comms.tx.SendPrintFileStart;
import celtech.roboxbase.comms.tx.StatusRequest;
import celtech.roboxbase.comms.tx.WritePrinterID;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.configuration.fileRepresentation.CameraSettings;
import com.codahale.metrics.annotation.Timed;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
@RolesAllowed("root")
@Path("/{printerID}" + Configuration.lowLevelAPIService)
@Produces(MediaType.APPLICATION_JSON)
public class LowLevelAPI
{

    private final Stenographer steno = StenographerFactory.getStenographer(LowLevelAPI.class.getName());

    public LowLevelAPI()
    {
    }

    @POST
    @Timed
    @Path(Configuration.connectService)
    public Response connect(@PathParam("printerID") String printerID)
    {
        if (Root.isResponding()) {
            steno.info("Was asked to connect to " + printerID);
            return Response.ok().build();
        }
        else
            return Response.serverError().status(503).build();
    }

    @POST
    @Timed
    @Path(Configuration.disconnectService)
    public Response disconnect(@PathParam("printerID") String printerID)
    {
        if (Root.isResponding()) {
            steno.info("Was asked to disconnect from " + printerID);
            return Response.ok().build();
        }
        else
            return Response.serverError().status(503).build();
    }

    @POST
    @Timed
    @Path(Configuration.writeDataService)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RoboxRxPacket writeToPrinter(@PathParam("printerID") String printerID,
            RoboxTxPacket remoteTx)
    {
        if (Root.isResponding()) {
            RoboxRxPacket rxPacket = null;
            long t1 = System.currentTimeMillis();
    //        steno.info("Request to write to printer with ID " + printerID + " and packet type " + remoteTx.getPacketType());
    //        String messagePayload = remoteTx.getMessagePayload();//
    //        if (messagePayload != null)
    //             steno.info("    Payload length " + Integer.toString(messagePayload.length()));
    //        if (remoteTx.getIncludeSequenceNumber())
    //             steno.info("    Sequence number = " + Integer.toString(remoteTx.getSequenceNumber()));
            try
            {
                if (PrinterRegistry.getInstance() != null
                        && !PrinterRegistry.getInstance().getRemotePrinterIDs().contains(printerID))
                {
                    rxPacket = RoboxRxPacketFactory.createPacket(RxPacketTypeEnum.PRINTER_NOT_FOUND);
                } else
                {
                    if (remoteTx instanceof StatusRequest)
                    {
                        rxPacket = PrinterRegistry.getInstance().getRemotePrinters().get(printerID).getLastStatusResponse();
                    } else if (remoteTx instanceof ReportErrors)
                    {
                       AckResponse ackResponse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID).getLastErrorResponse();
                        // The firmware errors in the last response will be empty because it has already been processed by Root.
                        // So replace it with the list of active errors.
            //            ackResponse.setFirmwareErrors(PrinterRegistry.getInstance().getRemotePrinters().get(printerID).getActiveErrors());
                        ackResponse.setFirmwareErrors(PrinterRegistry.getInstance().getRemotePrinters().get(printerID).getCurrentErrors());
                        rxPacket = ackResponse;
                     } else if (remoteTx instanceof ReadSendFileReport
                            && PrinterRegistry.getInstance().getRemotePrinters().get(printerID).getPrintEngine().highIntensityCommsInProgressProperty().get())
                    {
                        rxPacket = RoboxRxPacketFactory.createPacket(RxPacketTypeEnum.SEND_FILE);
                    }
                    else
                    {
                        try
                        {
                            rxPacket = PrinterRegistry.getInstance().getRemotePrinters().get(printerID).getCommandInterface().writeToPrinter(remoteTx, false);
                        } catch (RoboxCommsException ex)
                        {
                            steno.error("Failed whilst writing to local printer with ID" + printerID);
                        }

                        if (remoteTx instanceof SendPrintFileStart
                                || remoteTx instanceof SendDataFileStart)
                        {
                            PrintJobPersister.getInstance().startFile(remoteTx.getMessagePayload());
                            PrinterRegistry.getInstance().getRemotePrinters().get(printerID).getPrintEngine().takingItThroughTheBackDoor(true);
                        } else if (remoteTx instanceof SendDataFileChunk)
                        {
                            String payload = remoteTx.getMessagePayload();
                            PrintJobPersister.getInstance().writeSegment(payload);
                        } else if (remoteTx instanceof SendDataFileEnd)
                        {
                            PrintJobPersister.getInstance().closeFile(remoteTx.getMessagePayload());
                            PrinterRegistry.getInstance().getRemotePrinters().get(printerID).getPrintEngine().takingItThroughTheBackDoor(false);
                        }
                        else if (remoteTx instanceof WritePrinterID &&
                                  PrinterRegistry.getInstance().getRemotePrinters().size() == 1 &&
                                  PrinterRegistry.getInstance().getRemotePrinters().get(printerID).printerConfigurationProperty().get().getTypeCode().equals("RBX10"))
                        {
                            // If only one printer is connected and it is an RBX10, then this is a Robox Pro printer.
                            // Keep the server name the same as the printer name.
                            WritePrinterID wpid = ((WritePrinterID) remoteTx);
                            PrinterRegistry.getInstance().setServerName(wpid.getPrinterFriendlyName());                    
                        }
                    }
                }

            }
            catch (Exception ex)
            {
                long t2 = System.currentTimeMillis();
                steno.error("LowLevelAPI.writeToPrinter() after " + (t2 - t1) + "ms caught exception " + ex.getClass().getCanonicalName() + " with message " + ex.getMessage());
                throw ex;
            }

            long t2 = System.currentTimeMillis();
            //if (rxPacket == null)
            //    steno.info("Returning null packet after " + (t2 - t1) + "ms");
            //else
            //    steno.info("Returning packet " + rxPacket.getPacketType() + " after " + (t2 - t1) + "ms");
            return rxPacket;
        }
        else
            return null;
    }

    @RolesAllowed("root")
    @POST
    @Timed
    @Path(Configuration.sendStatisticsService)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response provideStatistics(@PathParam("printerID") String printerID,
            PrintJobStatistics statistics)
    {
        Response response = null;
        if (Root.isResponding()) {
            String statsFileLocation = BaseConfiguration.getPrintSpoolDirectory() + statistics.getPrintJobID() + File.separator + statistics.getPrintJobID() + BaseConfiguration.statisticsFileExtension;
            try
            {
                steno.info("Writing statistics to file \"" + statsFileLocation + "\" ...");
                statistics.writeStatisticsToFile(statsFileLocation);
                steno.info("... done");
                response = Response.ok().build();
            } catch (IOException ex)
            {
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
    @Path(Configuration.retrieveStatisticsService)
    @Produces(MediaType.APPLICATION_JSON)
    public PrintJobStatistics retrieveStatistics(@PathParam("printerID") String printerID,
                                                 String printJobID)
    {
        PrintJobStatistics statistics = null;
        if (Root.isResponding()) {
            try
            {
                statistics = PrinterRegistry.getInstance().getRemotePrinters().get(printerID).getPrintEngine().printJobProperty().get().getStatistics();
            } catch (IOException ex)
            {

            }
        }
        return statistics;
    }

    @RolesAllowed("root")
    @POST
    @Timed
    @Path("/{printJobID}" + Configuration.sendCameraDataService)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response provideCameraData(@PathParam("printerID") String printerID,
                                         @PathParam("printJobID") String printJobID,
                                         CameraSettings cameraData)
    {
        Response response = null;
        if (Root.isResponding()) {
            String dataFileLocation = BaseConfiguration.getPrintSpoolDirectory() + printJobID + File.separator + printJobID + BaseConfiguration.cameraDataFileExtension;
            try
            {
                steno.info("Writing camera data to file \"" + dataFileLocation + "\" ...");
                cameraData.writeToFile(dataFileLocation);
                steno.info("... done");
                response = Response.ok().build();
            } 
            catch (IOException ex) {
                response = Response.serverError().build();
            }
        }
        else
            response = Response.serverError().status(503).build();
        
        return response;
    }

    @RolesAllowed("root")
    @GET
    @Timed
    @Path("/{printJobID}" + Configuration.retrieveCameraDataService)
    @Produces(MediaType.APPLICATION_JSON)
    public CameraSettings retrieveCameraData(@PathParam("printerID") String printerID,
                                             @PathParam("printJobID") String printJobID)
    {
        CameraSettings cameraData = null;
        if (Root.isResponding()) {
            String dataFileLocation = BaseConfiguration.getPrintSpoolDirectory() + printJobID + File.separator + printJobID + BaseConfiguration.cameraDataFileExtension;
            try
            {
                    //steno.info("Reading camera data from file \"" + dataFileLocation + "\" ...");
                    cameraData= CameraSettings.readFromFile(dataFileLocation);
                    //steno.info("... done");
            }
            catch (IOException ex) {
            }
        }
        return cameraData;
    }

    @RolesAllowed("root")
    @POST
    @Timed
    @Path(Configuration.overrideFilamentService)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response overrideFilament(@PathParam("printerID") String printerID,
            Map<Integer, String> filamentMap)
    {
        Response response = null;
        if (Root.isResponding()) {
            Entry<Integer, String> filamentEntry = filamentMap.entrySet().iterator().next();
            Filament chosenFilament = FilamentContainer.getInstance().getFilamentByID(filamentEntry.getValue());
            if (chosenFilament != null)
            {
                PrinterRegistry.getInstance().getRemotePrinters().get(printerID).overrideFilament(filamentEntry.getKey(), chosenFilament);
                response = Response.ok().build();
            } else
            {
                response = Response.serverError().build();
            }
        }
        else
            response = Response.serverError().status(503).build();
        return response;
    }
}
