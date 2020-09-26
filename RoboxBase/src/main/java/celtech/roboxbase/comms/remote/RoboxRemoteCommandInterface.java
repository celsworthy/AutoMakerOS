package celtech.roboxbase.comms.remote;

import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.comms.CommandInterface;
import celtech.roboxbase.comms.PrinterStatusConsumer;
import celtech.roboxbase.comms.RemoteDetectedPrinter;
import celtech.roboxbase.comms.exceptions.ConnectionLostException;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.rx.FirmwareError;
import celtech.roboxbase.comms.rx.PrinterNotFound;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import celtech.roboxbase.comms.tx.RoboxTxPacket;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.fileRepresentation.CameraSettings;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.utils.tasks.Cancellable;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class RoboxRemoteCommandInterface extends CommandInterface
{

    private final RemoteClient remoteClient;

    public RoboxRemoteCommandInterface(PrinterStatusConsumer controlInterface,
            RemoteDetectedPrinter printerHandle,
            boolean suppressPrinterIDChecks, int sleepBetweenStatusChecks)
    {
        super(controlInterface, printerHandle, suppressPrinterIDChecks, sleepBetweenStatusChecks, false);
        this.setName("RemoteCI:" + printerHandle.getConnectionHandle() + " " + this.getName());
        remoteClient = new RemoteClient(printerHandle);
    }

    @Override
    protected boolean connectToPrinterImpl()
    {
        boolean success = false;
        try
        {
            remoteClient.connect(printerHandle.getConnectionHandle());
            success = true;
        } catch (RoboxCommsException ex)
        {
            steno.error("Failed to connect to printer");
        }
        return success;
    }

    @Override
    protected void disconnectPrinterImpl()
    {
        try
        {
            remoteClient.disconnect(printerHandle.getConnectionHandle());
        } catch (RoboxCommsException ex)
        {
            steno.error("Failed to disconnect from printer");
        }
    }

    @Override
    public synchronized RoboxRxPacket writeToPrinterImpl(RoboxTxPacket messageToWrite,
            boolean dontPublishResult) throws RoboxCommsException
    {
        RoboxRxPacket rxPacket = remoteClient.writeToPrinter(printerHandle.getConnectionHandle(), messageToWrite);

        if (rxPacket != null)
        {
            if (rxPacket instanceof PrinterNotFound)
            {
                actionOnCommsFailure();
            } else if (!dontPublishResult)
            {
                printerToUse.processRoboxResponse(rxPacket);
            }
        }

        return rxPacket;
    }

    private void actionOnCommsFailure() throws ConnectionLostException
    {
        //If we get an exception then abort and treat
        steno.debug("Error during write to printer");
        shutdown();
        throw new ConnectionLostException();
    }

    void setPrinterToUse(Printer newPrinter)
    {
        this.printerToUse = newPrinter;
    }

    /**
     *
     * @param sleepMillis
     */
    @Override
    public void setSleepBetweenStatusChecks(int sleepMillis)
    {
        sleepBetweenStatusChecks = sleepMillis;
    }

    @Override
    public void clearAllErrors()
    {
        try
        {
            remoteClient.clearAllErrors(printerHandle.getConnectionHandle());
        }
        catch (RoboxCommsException ex)
        {
        }
    }

    @Override
    public void clearError(FirmwareError error)
    {
        try
        {
            remoteClient.clearError(printerHandle.getConnectionHandle(), error);
        }
        catch (RoboxCommsException ex)
        {
        }
    }
    
    public boolean cancelPrint(boolean safetyOn)
    {
        boolean success = true;
        try
        {
            remoteClient.cancelPrint(printerHandle.getConnectionHandle(), safetyOn);
        }
        catch (RoboxCommsException ex)
        {
            success = false;
        }
        
        return success;
    }

    public void sendStatistics(PrintJobStatistics printJobStatistics) throws RoboxCommsException
    {
        remoteClient.sendStatistics(printerHandle.getConnectionHandle(), printJobStatistics);
    }

    public PrintJobStatistics retrieveStatistics() throws RoboxCommsException
    {
        return remoteClient.retrieveStatistics(printerHandle.getConnectionHandle());
    }

    public void sendCameraData(String printJobID, CameraSettings cameraData) throws RoboxCommsException
    {
        remoteClient.sendCameraData(printerHandle.getConnectionHandle(), printJobID, cameraData);
    }

    public CameraSettings retrieveCameraData(String printJobID) throws RoboxCommsException
    {
        return remoteClient.retrieveCameraData(printerHandle.getConnectionHandle(), printJobID);
    }

    public void overrideFilament(int reelNumber, Filament filament) throws RoboxCommsException
    {
        remoteClient.overrideFilament(printerHandle.getConnectionHandle(), reelNumber, filament);
    }
    
    public void startPrintJob(String printJobID) throws RoboxCommsException
    {
        remoteClient.startPrintJob(printerHandle.getConnectionHandle(), printJobID);
    }

    public void printGCodeFile(String remoteFileName) throws RoboxCommsException
    {
        remoteClient.printGCodeFile(printerHandle.getConnectionHandle(), remoteFileName);
    }

}
