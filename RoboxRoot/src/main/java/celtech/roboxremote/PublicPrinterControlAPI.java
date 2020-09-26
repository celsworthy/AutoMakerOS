package celtech.roboxremote;

import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.remote.Configuration;
import celtech.roboxbase.comms.rx.FirmwareError;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.Macro;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.HeaterMode;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import celtech.roboxbase.utils.PrinterUtils;
import celtech.roboxbase.utils.tasks.SimpleCancellable;
import celtech.roboxremote.rootDataStructures.ActiveErrorStatusData;
import celtech.roboxremote.rootDataStructures.ControlStatusData;
import celtech.roboxremote.rootDataStructures.HeadEEPROMData;
import celtech.roboxremote.rootDataStructures.HeadStatusData;
import celtech.roboxremote.rootDataStructures.MaterialStatusData;
import celtech.roboxremote.rootDataStructures.NameStatusData;
import celtech.roboxremote.rootDataStructures.NameTagFloat;
import celtech.roboxremote.rootDataStructures.PrintAdjustData;
import celtech.roboxremote.rootDataStructures.PrintJobStatusData;
import celtech.roboxremote.rootDataStructures.PurgeTarget;
import celtech.roboxremote.rootDataStructures.StatusData;
import celtech.roboxremote.rootDataStructures.SuitablePrintJobListData;
import celtech.roboxremote.rootDataStructures.UsbPrintData;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.jersey.params.BooleanParam;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.paint.Color;
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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author Ian
 */
@RolesAllowed("root")
@Path("/{printerID}" + Configuration.publicAPIService)
@Produces(MediaType.APPLICATION_JSON)
public class PublicPrinterControlAPI
{

    private final Stenographer STENO = StenographerFactory.getStenographer(PublicPrinterControlAPI.class.getName());
    private final Utils utils = new Utils();
    private SimpleCancellable purgeCancel = null;

    public PublicPrinterControlAPI()
    {
    }

    @GET
    @Timed
    public StatusData getPrinterStatus(@PathParam("printerID") String printerID)
    {
        StatusData returnVal = null;
        try {
            if (Root.isResponding() &&
                PrinterRegistry.getInstance() != null &&
                PrinterRegistry.getInstance().getRemotePrinters() != null &&
                PrinterRegistry.getInstance().getRemotePrinters().get(printerID) != null)
            {
                returnVal = new StatusData();
                returnVal.updateFromPrinterData(printerID);
            }
            else
            {
                STENO.error("Unrecognised printer " + printerID);
            }
        }
        catch(Exception ex) {
            STENO.exception("Exception whilst getting status of printer " + printerID, ex);
        }
        return returnVal;
    }

    @GET
    @Timed
    @Path("headEEPROM")
    public HeadEEPROMData getHeadEEPROMData(@PathParam("printerID") String printerID)
    {
        HeadEEPROMData returnVal = null;
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            returnVal = new HeadEEPROMData();
            returnVal.updateFromPrinterData(printerID);
        }
        return returnVal;
    }

    @GET
    @Timed
    @Path("headStatus")
    public HeadStatusData getHeadStatus(@PathParam("printerID") String printerID)
    {
        HeadStatusData returnVal = null;
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            returnVal = new HeadStatusData();
            returnVal.updateFromPrinterData(printerID);
        }
        return returnVal;
    }

    @GET
    @Timed
    @Path("materialStatus")
    public MaterialStatusData getMaterialStatus(@PathParam("printerID") String printerID)
    {
        MaterialStatusData returnVal = null;
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            returnVal = new MaterialStatusData();
            returnVal.updateFromPrinterData(printerID);
        }
        return returnVal;
    }

    @GET
    @Timed
    @Path("nameStatus")
    public NameStatusData getNameStatus(@PathParam("printerID") String printerID)
    {
        NameStatusData returnVal = null;
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            returnVal = new NameStatusData();
            returnVal.updateFromPrinterData(printerID);
        }
        return returnVal;
    }

    @GET
    @Timed
    @Path("printJobStatus")
    public PrintJobStatusData getPrintJobStatus(@PathParam("printerID") String printerID)
    {
        PrintJobStatusData returnVal = null;
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            returnVal = new PrintJobStatusData();
            returnVal.updateFromPrinterData(printerID);
        }
        return returnVal;
    }

    @GET
    @Timed
    @Path("printAdjust")
    public PrintAdjustData getPrintAdjust(@PathParam("printerID") String printerID)
    {
        PrintAdjustData returnVal = null;
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            returnVal = new PrintAdjustData();
            returnVal.updateFromPrinterData(printerID);
        }
        return returnVal;
    }

    @GET
    @Timed
    @Path("controlStatus")
    public ControlStatusData getControlStatus(@PathParam("printerID") String printerID)
    {
        ControlStatusData returnVal = null;
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            returnVal = new ControlStatusData();
            returnVal.updateFromPrinterData(printerID);
        }
        return returnVal;
    }
    
    @GET
    @Timed
    @Path("activeErrorStatus")
    public ActiveErrorStatusData getActiveErrorStatus(@PathParam("printerID") String printerID)
    {
        ActiveErrorStatusData returnVal = null;
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            returnVal = new ActiveErrorStatusData();
            returnVal.updateFromPrinterData(printerID);
        }
        return returnVal;
    }

    @POST
    @Timed
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("printGCodeFile")
    public Response printGCodeFile(@PathParam("printerID") String printerID,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException
    {
        if (Root.isResponding()) {
            String uploadedFileLocation = BaseConfiguration.getPrintSpoolDirectory() + printerID + fileDetail.getFileName();
            STENO.debug("Printing gcode file " + uploadedFileLocation);
            // save it
            utils.writeToFile(uploadedInputStream, uploadedFileLocation);

            try
            {
                PrinterRegistry.getInstance().getRemotePrinters().get(printerID).executeGCodeFile(uploadedFileLocation, true);
            } catch (PrinterException ex)
            {
                STENO.exception("Exception whilst trying to print gcode file " + uploadedFileLocation, ex);
            }
            return Response.ok().build();
        }
        else
            return Response.status(503).build();
    }
    
    @POST
    @Timed
    @Path("setHeadEEPROM")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setNozzleParams(@PathParam("printerID") String printerID,
                                    HeadEEPROMData eData)
    {
        Response response = null;
        try
        {
            if (Root.isResponding()) {
                Printer printer = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
                if (printer != null)
                {
                    if (eData.getNozzleCount() > 1)
                    {
                        // If there are two nozzles, then the left one comes first.
                        float leftNozzleZOffset = PrinterUtils.deriveNozzle1ZOffsetsFromOverrun((float)eData.getLeftNozzleZOverrun(), (float)eData.getRightNozzleZOverrun());
                        float rightNozzleZOffset = PrinterUtils.deriveNozzle2ZOffsetsFromOverrun((float)eData.getLeftNozzleZOverrun(), (float)eData.getRightNozzleZOverrun());

                        printer.transmitWriteHeadEEPROM(
                            eData.getTypeCode(),
                            eData.getUniqueID(),
                            (float)eData.getMaxTemp(),
                            (float)eData.getBeta(),
                            (float)eData.getTCal(),
                            (float)eData.getLeftNozzleXOffset(),
                            (float)eData.getLeftNozzleYOffset(),
                            leftNozzleZOffset,
                            (float)eData.getLeftNozzleBOffset(),
                            "",
                            "",
                            (float)eData.getRightNozzleXOffset(),
                            (float)eData.getRightNozzleYOffset(),
                            rightNozzleZOffset,
                            (float)eData.getRightNozzleBOffset(),
                            (float)eData.getLeftNozzleLastFTemp(),
                            (float)eData.getRightNozzleLastFTemp(),
                            (float)eData.getHourCount());
                    }
                    else
                    {
                        // If there is only one nozzle, it is the right one, and should come first.
                        float rightNozzleZOffset = PrinterUtils.deriveNozzle2ZOffsetsFromOverrun((float)eData.getRightNozzleZOverrun(), (float)eData.getRightNozzleZOverrun());
                        printer.transmitWriteHeadEEPROM(
                            eData.getTypeCode(),
                            eData.getUniqueID(),
                            (float)eData.getMaxTemp(),
                            (float)eData.getBeta(),
                            (float)eData.getTCal(),
                            (float)eData.getRightNozzleXOffset(),
                            (float)eData.getRightNozzleYOffset(),
                            rightNozzleZOffset,
                            (float)eData.getRightNozzleBOffset(),
                            "",
                            "",
                            -1.0F,
                            -1.0F,
                            rightNozzleZOffset,
                            -1.0F,
                            (float)eData.getRightNozzleLastFTemp(),
                            -1.0F,
                            (float)eData.getHourCount());
                    }
                    response = Response.ok().build();
                }
            }
            else
                response = Response.serverError().status(503).build();
        } catch (RoboxCommsException ex)
        {
        }
        finally
        {
            if (response == null)
                response = Response.serverError().build();
        }
        
        return response;
    }

    @POST
    @Timed
    @Path("setPrintAdjust")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setPrintAdjust(@PathParam("printerID") String printerID,
                                   NameTagFloat ntfData)
    {
        Response response = null;

        if (Root.isResponding()) {
            boolean ok = true;
            try
            {
                Printer printer = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
                if (printer != null)
                {
                    switch(ntfData.getName().toLowerCase())
                    {
                        case "temp":
                            switch(ntfData.getTag().toLowerCase())
                            {
                                case "bed":
                                    printer.setBedTargetTemperature(Math.round(ntfData.getValue()));
                                    break;
                                case "r":
                                    if (printer.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
                                    {
                                        printer.setNozzleHeaterTargetTemperature(1, Math.round(ntfData.getValue()));
                                    } else
                                    {
                                        printer.setNozzleHeaterTargetTemperature(0, Math.round(ntfData.getValue()));
                                    }
                                    break;
                                case "l":
                                    printer.setNozzleHeaterTargetTemperature(0, Math.round(ntfData.getValue()));
                                    break;
                                default:
                                    ok = false;
                            }  
                            break;
                        case "feedrate":
                            switch(ntfData.getTag().toLowerCase())
                            {
                                case "r":
                                    printer.changeEFeedRateMultiplier(0.01 * ntfData.getValue());
                                    STENO.debug("Setting R feed rate to " + 0.01 * ntfData.getValue());
                                    STENO.debug("feedRateEMultiplierProperty now " + printer.getPrinterAncillarySystems().feedRateEMultiplierProperty().floatValue() * 100.0F);
                                    break;
                                case "l":
                                    printer.changeDFeedRateMultiplier(0.01 * ntfData.getValue());
                                    STENO.debug("Setting L feed rate to " + 0.01 * ntfData.getValue());
                                    STENO.debug("feedRateDMultiplierProperty now " + printer.getPrinterAncillarySystems().feedRateDMultiplierProperty().floatValue() * 100.0F);
                                    break;
                                default:
                                    ok = false;
                            }  
                            break;
                        case "extrusionrate":
                            switch(ntfData.getTag().toLowerCase())
                            {
                                case "r":
                                    printer.changeFilamentInfo("E", printer.extrudersProperty().get(0).filamentDiameterProperty().get(), 0.01 * ntfData.getValue());
                                    break;
                                case "l":
                                    printer.changeFilamentInfo("D", printer.extrudersProperty().get(1).filamentDiameterProperty().get(), 0.01 * ntfData.getValue());
                                    break;
                                default:
                                    ok = false;
                            }  
                            break;
                        default:
                            ok = false;
                    }
                }
            }
            catch (PrinterException ex)
            {
                ok = false;
            }
            if (ok)
                response = Response.ok().build();
            else
                response = Response.serverError().build();
        }
        else
            response = Response.serverError().status(503).build();
        
        return response;
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/openDoor")
    public boolean openDoor(@PathParam("printerID") String printerID, BooleanParam safetyOn)
    {
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            try
            {
                PrinterRegistry.getInstance().getRemotePrinters().get(printerID).goToOpenDoorPosition(null, safetyOn.get());
            } catch (PrinterException ex)
            {
                STENO.error("Exception whilst opening door");
                return false;
            }
        }

        return true;
    }

    @POST
    @Timed
    @Path("/pause")
    public void pause(@PathParam("printerID") String printerID)
    {
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            try
            {
                PrinterRegistry.getInstance().getRemotePrinters().get(printerID).pause();
            } catch (PrinterException ex)
            {
                STENO.error("Exception whilst pausing");
            }
        }
    }

    @POST
    @Timed
    @Path("/resume")
    public void resume(@PathParam("printerID") String printerID)
    {
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            try
            {
                Printer p = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
                if (p != null && p.canResumeProperty().get())
                    p.resume();
            } catch (PrinterException ex)
            {
                STENO.error("Exception whilst resuming");
            }
        }
    }

    @POST
    @Timed
    @Path("/cancel")
    public void cancel(@PathParam("printerID") String printerID, BooleanParam safetyOn)
    {
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            try
            {
                Printer p = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
                if (p != null)
                {
                    if (p.canCancelProperty().get())
                        p.cancel(null, safetyOn.get());
                    else
                    {
                        cancelRunningPurge(false);
                        if (p.getPrinterAncillarySystems().bedHeaterModeProperty().get() != HeaterMode.OFF)
                            p.sendRawGCode("M140 S0\n", false);

                        Head head = p.headProperty().get();
                        if (head != null && head.getNozzleHeaters().size() > 0)
                        {
                            if (head.getNozzleHeaters().size() == 1 &&
                                head.getNozzleHeaters().get(0).heaterModeProperty().get() != HeaterMode.OFF)
                            {
                                p.sendRawGCode("M104 S0\n", false);
                            }
                            else
                            {
                                if (head.getNozzleHeaters().get(0).heaterModeProperty().get() != HeaterMode.OFF)
                                    p.sendRawGCode("M104 S0\n", false);
                                if (head.getNozzleHeaters().size() > 1 &&
                                    head.getNozzleHeaters().get(1).heaterModeProperty().get() != HeaterMode.OFF)
                                {
                                    p.sendRawGCode("M104 T0\n", false);
                                }
                            }
                        }
                    }
                }
            }
            catch (PrinterException ex)
            {
                STENO.error("Exception whilst cancelling");
            }
        }
    }

    private synchronized SimpleCancellable cancelRunningPurge(boolean reset)
    {
        if (purgeCancel != null)
        {
            purgeCancel.cancelled().set(true);
            purgeCancel = null;
        }
        
        if (reset)
        {
            purgeCancel = new SimpleCancellable();
            purgeCancel.cancelled().set(false);
        }
        
        return purgeCancel;
    }
    
    private void doPurge(String printerID, int targetTemperature0, int targetTemperature1, boolean safetyOn)
    {
        if (PrinterRegistry.getInstance() != null)
        {
            Printer printer = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
            if (printer != null)
            {
                Thread purgeThread = new Thread(() ->
                {
                    STENO.debug("Starting purge.");
                    SimpleCancellable cancel = cancelRunningPurge(true);
                    int nozzle0Temperature = targetTemperature0;
                    int nozzle1Temperature = targetTemperature1;

                    if (printer.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
                    {
                        if (nozzle1Temperature == 0 && printer.effectiveFilamentsProperty().get(0) != FilamentContainer.UNKNOWN_FILAMENT)
                        {
                            nozzle1Temperature = printer.effectiveFilamentsProperty().get(0).getNozzleTemperature();
                        }
                        if (nozzle0Temperature == 0 && printer.effectiveFilamentsProperty().get(1) != FilamentContainer.UNKNOWN_FILAMENT)
                        {
                            nozzle0Temperature = printer.effectiveFilamentsProperty().get(1).getNozzleTemperature();
                        }
                    }
                    else // Single material head
                    {
                        // This very confusing. For a single material head, always use the left nozzle, with material 0.
                        // even though it may only have one nozzle, which is on the right.
                        if (nozzle1Temperature > 0)
                            nozzle0Temperature = nozzle1Temperature;
                        else if ((nozzle1Temperature == 0 && nozzle0Temperature <= 0) || nozzle0Temperature == 0)
                            nozzle0Temperature = printer.effectiveFilamentsProperty().get(0).getNozzleTemperature();
                        nozzle1Temperature= -1;
                    }
                    
                    boolean purgeLeftNozzle = (nozzle0Temperature > 0);
                    boolean purgeRightNozzle = (nozzle1Temperature > 0);

                    try
                    {
                        //Set the bed to 90 degrees C
                        if (cancel.cancelled().get())
                            return;
                        int desiredBedTemperature = 90;
                        printer.setBedTargetTemperature(desiredBedTemperature);
                        printer.goToTargetBedTemperature();
                        boolean bedHeatFailed = PrinterUtils.waitUntilTemperatureIsReached(
                                printer.getPrinterAncillarySystems().bedTemperatureProperty(), null,
                                desiredBedTemperature, 5, 600, cancel);

                        if (!bedHeatFailed && !cancel.cancelled().get())
                        {
                            if (purgeLeftNozzle)
                            {
                                printer.setNozzleHeaterTargetTemperature(0, nozzle0Temperature);
                                printer.goToTargetNozzleHeaterTemperature(0);
                            }

                            if (purgeRightNozzle)
                            {
                                printer.setNozzleHeaterTargetTemperature(1, nozzle1Temperature);
                                printer.goToTargetNozzleHeaterTemperature(1);
                            }

                            boolean nozzleHeatFailed = false;

                            if (purgeLeftNozzle && !cancel.cancelled().get())
                            {
                                nozzleHeatFailed = PrinterUtils.waitUntilTemperatureIsReached(
                                        printer.headProperty().get().getNozzleHeaters().get(0).nozzleTemperatureProperty(),
                                        null, nozzle0Temperature, 5, 300, cancel);
                            }

                            if (purgeRightNozzle && !nozzleHeatFailed && !cancel.cancelled().get())
                            {
                                nozzleHeatFailed = PrinterUtils.waitUntilTemperatureIsReached(
                                        printer.headProperty().get().getNozzleHeaters().get(1).nozzleTemperatureProperty(),
                                        null, nozzle1Temperature, 5, 300, cancel);
                            }

                            if (!nozzleHeatFailed && !cancel.cancelled().get())
                            {
                                printer.purgeMaterial(purgeLeftNozzle, purgeRightNozzle, safetyOn, false, cancel);
                            }
                            else
                            {
                                STENO.info("Nozzle heat failed.");
                            }
                        }
                        else
                        {
                            STENO.info("Bed heat failed.");
                        }
                    } catch (PrinterException | InterruptedException ex)
                    {
                        STENO.error("Exception whilst purging");
                    }
                    finally
                    {
                        cancel.cancelled().set(true);
                        STENO.debug("Finishing purge.");                    
                    }
                });
                purgeThread.setName("Purge_" + printerID);
                purgeThread.run();
            }
        }
    }

    @POST
    @Timed
    @Path("/purge")
    public void purge(@PathParam("printerID") String printerID, BooleanParam safetyOn)
    {
        if (Root.isResponding())
            doPurge(printerID, 0, 0, safetyOn.get());
    }
    
    @POST
    @Timed
    @Path("/purgeToTarget")
    @Consumes(MediaType.APPLICATION_JSON)
    public void purgeToTarget(@PathParam("printerID") String printerID, PurgeTarget target)
    {
        if (Root.isResponding()) {
            int nozzle0Temperature = -1;
            int nozzle1Temperature = -1;
            int[] targetTemperature = target.getTargetTemperature();
            if (targetTemperature != null)
            {
                if (targetTemperature.length > 0)
                    nozzle0Temperature = targetTemperature[0];
                if (targetTemperature.length > 1)
                    nozzle1Temperature = targetTemperature[1];
            }
            doPurge(printerID, nozzle0Temperature, nozzle1Temperature, target.getSafetyOn());
        }
    }
    
    @POST
    @Timed
    @Path("/removeHead")
    public void removeHead(@PathParam("printerID") String printerID, BooleanParam safetyOn)
    {
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            try
            {
                PrinterRegistry.getInstance().getRemotePrinters().get(printerID).removeHead(null, safetyOn.get());
            } catch (PrinterException ex)
            {
                STENO.error("Exception whilst removing head");
            }
        }
    }

    @POST
    @Timed
    @Path("/clearAllErrors")
    public void clearAllErrors(@PathParam("printerID") String printerID)
    {
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
            PrinterRegistry.getInstance().getRemotePrinters().get(printerID).clearAllErrors();
    }

    @POST
    @Timed
    @Path("/clearError")
    public void clearError(@PathParam("printerID") String printerID, int errorCode)
    {
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
            PrinterRegistry.getInstance().getRemotePrinters().get(printerID).clearError(FirmwareError.fromBytePosition(errorCode));
    }

    /**
     *
     * Expects filament number to be 1 or 2
     *
     * @param printerID
     * @param filamentNumber
     */
    @POST
    @Timed
    @Path("/ejectFilament")
    public void ejectFilament(@PathParam("printerID") String printerID, int filamentNumber)
    {
        if (Root.isResponding() && PrinterRegistry.getInstance() != null) {
            try
            {
                PrinterRegistry.getInstance().getRemotePrinters().get(printerID).ejectFilament(filamentNumber - 1, null);
            } catch (PrinterException ex)
            {
                STENO.error("Exception whilst ejecting filament " + filamentNumber + ": " + ex);
            }
        }
    }

    /**
     *
     * Expects material number to be 1 or 2
     *
     * @param printerID
     * @param materialNumber
     * @param safetyOn
    */
    @POST
    @Timed
    @Path("/ejectStuckMaterial")
    public void ejectStuckMaterial(@PathParam("printerID") String printerID, int materialNumber)//, BooleanParam safetyOn)
    {
        if (Root.isResponding() && PrinterRegistry.getInstance() != null) {
            Printer selectedPrinter =  PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
            if (selectedPrinter != null && selectedPrinter.headProperty().get() != null)
            {
                try
                {
                    int nozzleNumber = -1;
                    Head.HeadType ht = selectedPrinter.headProperty().get().headTypeProperty().get();
                    if (materialNumber == 2 && ht == Head.HeadType.DUAL_MATERIAL_HEAD)
                        nozzleNumber = 0;
                    else if (materialNumber == 1)
                    {
                        switch (ht)
                        {
                            case DUAL_MATERIAL_HEAD:
                                nozzleNumber = 1;
                                break;

                            case SINGLE_MATERIAL_HEAD:
                                nozzleNumber = 0;
                                break;

                            default:
                                break;
                        }
                    }

                    if (nozzleNumber >= 0)
                        PrinterRegistry.getInstance().getRemotePrinters().get(printerID).ejectStuckMaterial(nozzleNumber, false, null, false);// safetyOn.get());
                    else
                        STENO.error("Invalid material number " + materialNumber);
                } 
                catch (PrinterException ex)
                {
                    STENO.error("Printer exception whilst ejecting stuck material" + materialNumber + ": " + ex);
                }
            }
        }
    }
    
    /**
     *
     * Expects requiredNozzle to be 1 (left) or 2 (right)
     *
     * @param printerID
     * @param nozzleNumber
     * @param safetyOn
     */
    @POST
    @Timed
    @Path("/cleanNozzle")
    public void cleanNozzle(@PathParam("printerID") String printerID, int requiredNozzle)//, BooleanParam safetyOn)
    {
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            int nozzleNumber = -1;
            Printer p = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
            Head h = p.headProperty().get();
            if (h != null &&
                h.valveTypeProperty().get() == Head.ValveType.FITTED &&
                h.getNozzles().size() > 0)
            {
                if (h.getNozzles().size() > 1)
                {
                    if (requiredNozzle == 1)
                        nozzleNumber = 0;
                    else if (requiredNozzle == 2)
                        nozzleNumber = 1;
                }
                else // h.getNozzles().size() == 1
                {
                    if (requiredNozzle == 2)
                        nozzleNumber = 0;
                }
            }
            
            if (nozzleNumber != -1)
            {
                try
                {
                    PrinterRegistry.getInstance().getRemotePrinters().get(printerID).cleanNozzle(nozzleNumber, false, null, false);// safetyOn.get());
                } catch (PrinterException ex)
                {
                    STENO.error("Printer exception whilst cleaning nozzle" + nozzleNumber + ": " + ex);
                }
            }
        }
    }
    
/**
     *
     * @param printerID
     * @param nozzleNumber
    */
    @POST
    @Timed
    @Path("/performTest")
    public void performTest(@PathParam("printerID") String printerID, String test)
    {
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            try
            {
                Printer p = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
                switch (Utils.cleanInboundJSONString(test).toLowerCase())
                {
                    case "x":
                        p.testX(false, null);
                        break;
                    case "y":
                        p.testY(false, null);
                        break;
                    case "z":
                        p.testZ(false, null);
                        break;
                    case "s":
                        p.speedTest(true, null);
                        break;
                        
                }
                
            } catch (PrinterException ex)
            {
                STENO.error("Exception whilst performing test " + test + ": " + ex);
            }
        }
    }

    @POST
    @Timed
    @Path("/listReprintableJobs")
    public SuitablePrintJobListData listReprintableJobs(@PathParam("printerID") String printerID)
    {
        SuitablePrintJobListData suitableJobData = new SuitablePrintJobListData();
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            Printer printerToUse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
            if (printerToUse != null)
            {
                List<PrintJobStatistics> printJobStats = printerToUse.listReprintableJobs();
                if (!printJobStats.isEmpty())
                {
                    suitableJobData.setJobs(printerToUse.createSuitablePrintJobsFromStatistics(printJobStats));
                    if (!suitableJobData.getJobs().isEmpty())
                        suitableJobData.setStatus(SuitablePrintJobListData.ListStatus.OK);
                    else
                        suitableJobData.setStatus(SuitablePrintJobListData.ListStatus.NO_SUITABLE_JOBS);
                }
                else
                    suitableJobData.setStatus(SuitablePrintJobListData.ListStatus.NO_JOBS);
            }
            else
                suitableJobData.setStatus(SuitablePrintJobListData.ListStatus.NO_PRINTER);
        }
        else
            suitableJobData.setStatus(SuitablePrintJobListData.ListStatus.ERROR);

        return suitableJobData;
    }
    
    @POST
    @Timed
    @Path("/listUSBPrintableJobs")
    public SuitablePrintJobListData listUSBPrintableJobs(@PathParam("printerID") String printerID) 
    {
        STENO.debug("API call made to " + printerID + "/remoteControl/listUSBPrintableJobs");
        
        SuitablePrintJobListData suitableJobData = new SuitablePrintJobListData();
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            Printer printerToUse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
            
            if (MountableMediaRegistry.getInstance() != null)
            {
                List<PrintJobStatistics> printJobStats = MountableMediaRegistry.getInstance().getPrintableProjectStats();
                if (!printJobStats.isEmpty())
                {
                    suitableJobData.setJobs(printerToUse.createSuitablePrintJobsFromStatistics(printJobStats));
                
                    if (!suitableJobData.getJobs().isEmpty())
                        suitableJobData.setStatus(SuitablePrintJobListData.ListStatus.OK);
                    else
                        suitableJobData.setStatus(SuitablePrintJobListData.ListStatus.NO_SUITABLE_JOBS);
                }
                else if (!MountableMediaRegistry.getInstance().getMountedUSBDirectories().isEmpty())
                    suitableJobData.setStatus(SuitablePrintJobListData.ListStatus.NO_JOBS);
                else
                    suitableJobData.setStatus(SuitablePrintJobListData.ListStatus.NO_MEDIA);
            }
            else
                suitableJobData.setStatus(SuitablePrintJobListData.ListStatus.NO_PRINTER);
        }
        else
            suitableJobData.setStatus(SuitablePrintJobListData.ListStatus.ERROR);
        
        return suitableJobData;
    }

    @POST
    @Timed
    @Path("/tidyPrintJobDirs")
    public Response tidyPrintJobDirectories(@PathParam("printerID") String printerID)
    {
        if (Root.isResponding()) {
            if (PrinterRegistry.getInstance() != null)
            {
                Printer printerToUse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
                printerToUse.tidyPrintJobDirectories();
            }

            return Response.ok().build();
        }
        else
            return Response.serverError().status(503).build();
    }

    @POST
    @Timed
    @Path("/reprintJob")
    public Response reprintJob(@PathParam("printerID") String printerID, String printJobID)
    {
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            Printer printerToUse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
            Platform.runLater(() ->
            {
                printerToUse.printJob(Utils.cleanInboundJSONString(printJobID));
            });
            return Response.ok().build();
        }
        else
            return Response.serverError().status(503).build();
    }
    
    @POST
    @Timed
    @Path("/printJob")
    public Response printJob(@PathParam("printerID") String printerID, String printJobID)
    {
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            Printer printerToUse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
            Platform.runLater(() ->
            {
                printerToUse.printJob(Utils.cleanInboundJSONString(printJobID));
            });
            return Response.ok().build();
        }
        else
            return Response.serverError().status(503).build();
    }
    
    @POST
    @Timed
    @Path("/printUSBJob")
    public Response printUSBJob(@PathParam("printerID") String printerID, UsbPrintData usbPrintData)
    {
        STENO.debug("Request to /printUSBJob with printer ID of - " + printerID + " and data - " + usbPrintData);
        
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            Printer printerToUse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
            Platform.runLater(() ->
            {
                printerToUse.printJobFromDirectory(usbPrintData.getPrintJobID(), usbPrintData.getPrintJobPath());
            });
            return Response.ok().build();
        }
        else
            return Response.serverError().status(503).build();
    }

    @POST
    @Timed
    @Path("/printGCodeFile")
    public Response printGCodeFile(@PathParam("printerID") String printerID, String fileName)
    {
        Response response = null;
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            try
            {
                Printer printerToUse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
                printerToUse.executeGCodeFile(Utils.cleanInboundJSONString(fileName), true);
                response = Response.ok().build();
            }
            catch (PrinterException ex)
            {
                STENO.error("Exception whilst printing GCode file \"" + fileName + "\": " + ex);
                response = Response.serverError().build();
            }
        }
        else
            response = Response.serverError().status(503).build();
        
        return response;
    }

    @POST
    @Timed
    @Path("/executeGCode")
    public Response executeGCode(@PathParam("printerID") String printerID, String gcode)
    {
        Response response = null;
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            String[] gcodeParts = Utils.cleanInboundJSONString(gcode).split(":");
            Printer printerToUse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
            
            List<String> transcript = new ArrayList<String>();
            for (String gcodePart : gcodeParts)
            {
                String t = printerToUse.sendRawGCode(gcodePart, false);
                if (t != null)
                    t = t.trim();
                if (!t.isEmpty())
                    transcript.add(t);
            }
            if (!transcript.isEmpty())
            {
                response = Response.ok(transcript).build();
            }
            else
                response = Response.ok().build();
        }
        else
            response = Response.serverError().status(503).build();
        return response;
    }
    
    @POST
    @Timed
    @Path("/getTranscript")
    public Response getTranscript(@PathParam("printerID") String printerID)
    {
        Response response = null;
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            
            Printer printerToUse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
            response = Response.ok(printerToUse.gcodeTranscriptProperty()).build();
        }
        else
            response = Response.serverError().status(503).build();
 
        return response;
    }

    @POST
    @Timed
    @Path("/clearTranscript")
    public Response clearTranscript(@PathParam("printerID") String printerID)
    {
        Response response = null;
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            Printer printerToUse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
            printerToUse.gcodeTranscriptProperty().clear();
            response = Response.ok().build();
        }
        else
            response = Response.serverError().status(503).build();
        return response;
    }

    @POST
    @Timed
    @Path("/runMacro")
    public Response runMacro(@PathParam("printerID") String printerID, String macroName)
    {
        Response response = null;

        //We should either just get a plain macro name or the macro|<T|F>|<T|F>|<T|F>
        //This represents Requires N1, Requires N2, Require safety features
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            String inputString = Utils.cleanInboundJSONString(macroName);
            String[] parts = inputString.split("\\|");
            Macro macroToRun = null;
            Printer printerToUse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);

            if (parts.length != 4)
            {
                macroToRun = Macro.valueOf(Utils.cleanInboundJSONString(macroName));
                try
                {
                    printerToUse.executeMacroWithoutPurgeCheck(macroToRun);
                    response = Response.ok().build();
                } 
                catch (PrinterException ex)
                {
                    STENO.exception("Exception whilst attempting to run macro with name " + macroName, ex);
                    response = Response.serverError().build();
                }
            } else
            {
                macroToRun = Macro.valueOf(parts[0]);
                boolean requiresN1 = parts[1].toLowerCase().equals("T");
                boolean requiresN2 = parts[2].toLowerCase().equals("T");
                boolean requiresSafeties = parts[3].toLowerCase().equals("T");
                try
                {
                    printerToUse.executeMacroWithoutPurgeCheck(macroToRun, requiresN1, requiresN2, requiresSafeties);
                    response = Response.ok().build();
                } 
                catch (PrinterException ex)
                {
                    STENO.exception("Exception whilst attempting to run macro with name " + macroName, ex);
                    response = Response.serverError().build();
                }
            }
        }
        else
            response = Response.serverError().status(503).build();

        return response;
    }

    @POST
    @Timed
    @Path("/renamePrinter")
    public Response renamePrinter(@PathParam("printerID") String printerID, String newName)
    {
        Response response = null;
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            Printer printerToUse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
            try
            {
                String cleanName = Utils.cleanInboundJSONString(newName);
                printerToUse.updatePrinterName(Utils.cleanInboundJSONString(cleanName));
                // If this printer is an RBX10, and is the only one connected to the serve,
                // assume it is a RoboxPro. Name the server to be the same as the printer.

                if (printerToUse.printerConfigurationProperty().get().getTypeCode().equals("RBX10") &&
                    PrinterRegistry.getInstance().getRemotePrinters().size() == 1)
                {
                    PrinterRegistry.getInstance().setServerName(cleanName);                    
                }
                response = Response.ok().build();
            } catch (PrinterException ex)
            {
                response = Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }
        }
        else
            response = Response.serverError().status(503).build();

        return response;
    }

    @POST
    @Timed
    @Path("/changePrinterColour")
    public Response changePrinterColour(@PathParam("printerID") String printerID, String newWebColour)
    {
        Response response = null;
        if (Root.isResponding() && PrinterRegistry.getInstance() != null)
        {
            Printer printerToUse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
            try
            {
                printerToUse.updatePrinterDisplayColour(Color.web(Utils.cleanInboundJSONString(newWebColour)));
                response = Response.ok().build();
            } catch (PrinterException ex)
            {
                response = Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }
        }
        else
            response = Response.serverError().status(503).build();

        return response;
    }

    @POST
    @Timed
    @Path("/setAmbientLED")
    public Response setAmbientLED(@PathParam("printerID") String printerID, String ledColour)
    {
        Response response = null;
        if (PrinterRegistry.getInstance() != null)
        {
            Printer printerToUse = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
            try
            {
                String cleanColour = Utils.cleanInboundJSONString(ledColour).toLowerCase();
                if (cleanColour.equals("on"))
                {
                    printerToUse.setAmbientLEDColour(printerToUse.getPrinterIdentity().printerColourProperty().get());
                }
                else if (cleanColour.equals("white"))
                {
                    printerToUse.setAmbientLEDColour(Color.WHITE);
                }
                else if (cleanColour.equals("off"))
                {
                    printerToUse.setAmbientLEDColour(Color.BLACK);
                }
                else
                {
                    printerToUse.setAmbientLEDColour(Color.web(cleanColour));
                }
                response = Response.ok().build();
            } catch (PrinterException ex)
            {
                response = Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }
        }
        else
            response = Response.serverError().status(503).build();
        
        return response;
    }
}
