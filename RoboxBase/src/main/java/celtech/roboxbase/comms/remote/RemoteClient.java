package celtech.roboxbase.comms.remote;

import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.comms.RemoteDetectedPrinter;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.rx.FirmwareError;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import celtech.roboxbase.comms.tx.RoboxTxPacket;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.fileRepresentation.CameraSettings;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.utils.SystemUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class RemoteClient implements LowLevelInterface
{

    private final Stenographer steno = StenographerFactory.getStenographer(RemoteClient.class.getName());
    private final RemoteDetectedPrinter remotePrinterHandle;

    private final String baseAPIString;
    private final String connectUrlString;
    private final String disconnectUrlString;
    private final String writeToPrinterUrlString;
    private final String sendStatisticsUrlString;
    private final String retrieveStatisticsUrlString;
    private final String overrideFilamentUrlString;
    private final String clearAllErrorsUrlString;
    private final String clearErrorUrlString;
    private final ObjectMapper mapper = new ObjectMapper();

    public RemoteClient(RemoteDetectedPrinter remotePrinterHandle)
    {
        this.remotePrinterHandle = remotePrinterHandle;
        baseAPIString = "/api";
        connectUrlString = Configuration.lowLevelAPIService + Configuration.connectService;
        disconnectUrlString = Configuration.lowLevelAPIService + Configuration.disconnectService;
        writeToPrinterUrlString = Configuration.lowLevelAPIService + Configuration.writeDataService;
        sendStatisticsUrlString = Configuration.lowLevelAPIService + Configuration.sendStatisticsService;
        retrieveStatisticsUrlString = Configuration.lowLevelAPIService + Configuration.retrieveStatisticsService;
        overrideFilamentUrlString = Configuration.lowLevelAPIService + Configuration.overrideFilamentService;
        clearAllErrorsUrlString = Configuration.publicAPIService + Configuration.clearAllErrorsService;
        clearErrorUrlString = Configuration.publicAPIService + Configuration.clearErrorService;
    }

    @Override
    public boolean connect(String printerID) throws RoboxCommsException
    {
        boolean success = false;
        try
        {
            remotePrinterHandle.getServerPrinterIsAttachedTo().postRoboxPacket(baseAPIString + "/" + printerID + connectUrlString);
        } catch (IOException ex)
        {
            steno.error("Failed to connect to remote printer " + remotePrinterHandle);
            throw new RoboxCommsException("Failed to connect to remote printer " + remotePrinterHandle);
        }
        return success;
    }

    @Override
    public void disconnect(String printerID) throws RoboxCommsException
    {
        try
        {
            remotePrinterHandle.getServerPrinterIsAttachedTo().postRoboxPacket(baseAPIString + "/" + printerID + disconnectUrlString);
        } catch (IOException ex)
        {
            steno.error("Failed to disconnect from remote printer " + remotePrinterHandle);
            throw new RoboxCommsException("Failed to disconnect from remote printer " + remotePrinterHandle);
        }
    }

    @Override
    public RoboxRxPacket writeToPrinter(String printerID, RoboxTxPacket messageToWrite) throws RoboxCommsException
    {
        RoboxRxPacket returnedPacket = null;

        try
        {
            //steno.info("remoteClient.writeToPrinter(" + printerID + ", " + messageToWrite.getPacketType().name());
            String dataToOutput = SystemUtils.jsonEscape(mapper.writeValueAsString(messageToWrite));
            
            returnedPacket = (RoboxRxPacket) remotePrinterHandle.getServerPrinterIsAttachedTo().postRoboxPacket(baseAPIString + "/" + printerID + writeToPrinterUrlString, dataToOutput, RoboxRxPacket.class);
            //steno.info("got response " + returnedPacket.getPacketType());
        } catch (JsonProcessingException ex)
        {
            steno.warning("Didn't get correct JSON from request - passing back null for " + messageToWrite.getPacketType().name());
        } catch (IOException ex)
        {
            steno.error("Failed to write to remote printer (" + messageToWrite.getPacketType().name() + ") " + remotePrinterHandle.getConnectionHandle() + " :" + ex.getMessage());
            throw new RoboxCommsException("Failed to write to remote printer (" + messageToWrite.getPacketType().name() + ") " + remotePrinterHandle.getConnectionHandle());
        }

        return returnedPacket;
    }

    public void clearAllErrors(String printerID) throws RoboxCommsException
    {
        try
        {
            remotePrinterHandle.getServerPrinterIsAttachedTo().postRoboxPacket(baseAPIString + "/" + printerID + clearAllErrorsUrlString, null, null);
        } catch (IOException ex)
        {
            steno.exception("Failed to clear all errors on remote printer " + remotePrinterHandle, ex);
            throw new RoboxCommsException("Failed to clear all errors on remote printer" + remotePrinterHandle);
        }
    }

    public void clearError(String printerID, FirmwareError error) throws RoboxCommsException
    {
        try
        {
            remotePrinterHandle.getServerPrinterIsAttachedTo().postRoboxPacket(baseAPIString + "/" + printerID + clearErrorUrlString, Integer.toString(error.getBytePosition()), null);
        } catch (IOException ex)
        {
            steno.exception("Failed to clear error " + error.toString() + " on remote printer " + remotePrinterHandle, ex);
            throw new RoboxCommsException("Failed to clear error " + error.toString() + " on remote printer " + remotePrinterHandle);
        }
    }

    public void sendStatistics(String printerID, PrintJobStatistics printJobStatistics) throws RoboxCommsException
    {
        try
        {
            String dataToOutput = SystemUtils.jsonEscape(mapper.writeValueAsString(printJobStatistics));
            remotePrinterHandle.getServerPrinterIsAttachedTo().postRoboxPacket(baseAPIString + "/" + printerID + sendStatisticsUrlString, dataToOutput, null);
        } catch (IOException ex)
        {
            steno.exception("Failed to send statistics to remote printer " + remotePrinterHandle, ex);
            throw new RoboxCommsException("Failed to send statistics to remote printer" + remotePrinterHandle);
        }
    }

    public PrintJobStatistics retrieveStatistics(String printerID) throws RoboxCommsException
    {
        PrintJobStatistics statistics = null;
        try
        {
            statistics = (PrintJobStatistics) remotePrinterHandle.getServerPrinterIsAttachedTo().postRoboxPacket(baseAPIString + "/" + printerID + retrieveStatisticsUrlString, null, PrintJobStatistics.class);
        } catch (IOException ex)
        {
            throw new RoboxCommsException("Failed to retrieve statistics from remote printer" + remotePrinterHandle.getServerPrinterIsAttachedTo().getServerIP());
        }

        return statistics;
    }

    public void sendCameraData(String printerID, String printJobID, CameraSettings cameraData) throws RoboxCommsException
    {
        try
        {
            String dataToOutput = SystemUtils.jsonEscape(mapper.writeValueAsString(cameraData));
        
            remotePrinterHandle.getServerPrinterIsAttachedTo().postRoboxPacket(baseAPIString + "/" + printerID + Configuration.lowLevelAPIService + "/" + printJobID + Configuration.sendCameraDataService, dataToOutput, null);
        } catch (IOException ex)
        {
            steno.exception("Failed to send statistics to remote printer " + remotePrinterHandle, ex);
            throw new RoboxCommsException("Failed to send statistics to remote printer" + remotePrinterHandle);
        }
    }

    public CameraSettings retrieveCameraData(String printerID, String printJobID) throws RoboxCommsException
    {
        CameraSettings cameraData = null;
        try
        {
            cameraData = (CameraSettings) remotePrinterHandle.getServerPrinterIsAttachedTo().postRoboxPacket(baseAPIString + "/" + printerID + Configuration.lowLevelAPIService + "/" + printJobID + Configuration.retrieveCameraDataService, null, CameraSettings.class);
        } catch (IOException ex)
        {
            throw new RoboxCommsException("Failed to retrieve statistics from remote printer" + remotePrinterHandle.getServerPrinterIsAttachedTo().getServerIP());
        }

        return cameraData;
    }

    public void overrideFilament(String printerID, int reelNumber, Filament filament) throws RoboxCommsException
    {
        Map<Integer, String> filamentMap = new HashMap();
        filamentMap.put(reelNumber, filament.getFilamentID());
        try
        {
            String jsonified = SystemUtils.jsonEscape(mapper.writeValueAsString(filamentMap));
            remotePrinterHandle.getServerPrinterIsAttachedTo().postRoboxPacket(baseAPIString + "/" + printerID + overrideFilamentUrlString, jsonified, null);
        } catch (IOException ex)
        {
            steno.error("Failed to override filament on remote printer " + remotePrinterHandle.getServerPrinterIsAttachedTo().getServerIP());
            throw new RoboxCommsException("Failed to override filament on remote printer" + remotePrinterHandle.getServerPrinterIsAttachedTo().getServerIP());
        }
    }
    
    public void startPrintJob(String printerID, String printJobID) throws RoboxCommsException
    {
        try
        {
            remotePrinterHandle.getServerPrinterIsAttachedTo().postRoboxPacket(baseAPIString + "/" + printerID + "/remoteControl/printJob", printJobID, null);
        } catch (IOException ex)
        {
            String message = "Failed to start print job \"" +
                             printJobID +
                             "\" on remote printer " +
                             remotePrinterHandle.getServerPrinterIsAttachedTo().getServerIP();
            steno.error(message);
            throw new RoboxCommsException(message);
        }
    }

    public void printGCodeFile(String printerID, String remoteFileName) throws RoboxCommsException
    {
        try
        {
            remotePrinterHandle.getServerPrinterIsAttachedTo().postRoboxPacket(baseAPIString + "/" + printerID + "/remoteControl/printGCodeFile", remoteFileName, null);
        } catch (IOException ex)
        {
            String message = "Failed to print GCode file \"" +
                             remoteFileName +
                             "\" on remote printer " +
                             remotePrinterHandle.getServerPrinterIsAttachedTo().getServerIP();
            steno.error(message);
            throw new RoboxCommsException(message);
        }
    }

    public void cancelPrint(String printerID, boolean safetyOn) throws RoboxCommsException
    {
        try
        {
            String jsonified = "\"" + mapper.writeValueAsString(safetyOn) + "\""; // Not sure why this needs to be in quotes, but it doesn't work without.
            remotePrinterHandle.getServerPrinterIsAttachedTo().postRoboxPacket(baseAPIString + "/" + printerID + "/remoteControl/cancel/", jsonified, null);
        } catch (IOException ex)
        {
            String message = "Failed to cancel print on remote printer " +
                             remotePrinterHandle.getServerPrinterIsAttachedTo().getServerIP();
            steno.error(message);
            throw new RoboxCommsException(message);
        }
    }
}
