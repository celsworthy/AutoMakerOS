package celtech.roboxbase.comms;

import celtech.roboxbase.ApplicationFeature;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.comms.async.AsyncWriteThread;
import celtech.roboxbase.comms.async.CommandPacket;
import celtech.roboxbase.comms.exceptions.PortNotFoundException;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.remote.RoboxRemoteCommandInterface;
import celtech.roboxbase.comms.rx.FirmwareError;
import celtech.roboxbase.comms.rx.FirmwareResponse;
import celtech.roboxbase.comms.rx.PrinterIDResponse;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import celtech.roboxbase.comms.rx.StatusResponse;
import celtech.roboxbase.comms.tx.RoboxTxPacket;
import celtech.roboxbase.comms.tx.RoboxTxPacketFactory;
import celtech.roboxbase.comms.tx.StatusRequest;
import celtech.roboxbase.comms.tx.TxPacketTypeEnum;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.CoreMemory;
import celtech.roboxbase.configuration.MachineType;
import celtech.roboxbase.configuration.datafileaccessors.PrinterContainer;
import celtech.roboxbase.configuration.fileRepresentation.PrinterDefinitionFile;
import celtech.roboxbase.configuration.fileRepresentation.PrinterEdition;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import celtech.roboxbase.services.firmware.FirmwareLoadResult;
import celtech.roboxbase.services.firmware.FirmwareLoadService;
import celtech.roboxbase.utils.PrinterUtils;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.paint.Color;
import libertysystems.configuration.ConfigItemIsAnArray;
import libertysystems.configuration.ConfigNotLoadedException;
import libertysystems.configuration.Configuration;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public abstract class CommandInterface extends Thread
{

    protected boolean keepRunning = true;

    protected Stenographer steno = StenographerFactory.getStenographer(
            HardwareCommandInterface.class.getName());
    protected PrinterStatusConsumer controlInterface = null;
    protected DetectedDevice printerHandle = null;
    protected Printer printerToUse = null;
    protected String printerFriendlyName = "Robox";
    protected RoboxCommsState commsState = RoboxCommsState.FOUND;
    protected PrinterID printerID = new PrinterID();

    protected final FirmwareLoadService firmwareLoadService = new FirmwareLoadService();
    protected String requiredFirmwareVersionString = "";
    protected float requiredFirmwareVersion = 0;
    protected float firmwareVersionInUse = 0;

    protected boolean suppressPrinterIDChecks = false;
    protected int sleepBetweenStatusChecks = 1000;
    private boolean loadingFirmware = false;

    protected boolean suspendStatusChecks = false;
    private final boolean localPrinter;

    private String printerName = null;

    private PrinterIDResponse lastPrinterIDResponse = null;

    private boolean isConnected = false;
    private int statusRequestCount = 0;
    private static final int maxAllowedStatusRequestCount = 3;

    private final AsyncWriteThread asyncWriteThread;

    /**
     *
     * @param controlInterface
     * @param printerHandle
     * @param suppressPrinterIDChecks
     * @param sleepBetweenStatusChecks
     */
    public CommandInterface(PrinterStatusConsumer controlInterface,
            DetectedDevice printerHandle,
            boolean suppressPrinterIDChecks,
            int sleepBetweenStatusChecks,
            boolean localPrinter)
    {
        this.controlInterface = controlInterface;
        this.printerHandle = printerHandle;
        this.suppressPrinterIDChecks = suppressPrinterIDChecks;
        this.sleepBetweenStatusChecks = sleepBetweenStatusChecks;
        this.localPrinter = localPrinter;
        
        this.setDaemon(true);
        this.setName("CommandInterface|" + printerHandle.getConnectionHandle());
        this.setPriority(8);

        asyncWriteThread = new AsyncWriteThread(this, printerHandle.getConnectionHandle());
        

        try
        {
            Configuration applicationConfiguration = Configuration.getInstance();
            try
            {
                requiredFirmwareVersionString = applicationConfiguration.getString(
                        BaseConfiguration.applicationConfigComponent, "requiredFirmwareVersion").
                        trim();
                requiredFirmwareVersion = Float.valueOf(requiredFirmwareVersionString);
            } catch (ConfigItemIsAnArray ex)
            {
                steno.error("Firmware version was an array... can't interpret firmware version");
            }
            catch (java.lang.NumberFormatException ex)
            {
                steno.error("Couldn't interpret firmware version as a number.");
            }
        } catch (ConfigNotLoadedException ex)
        {
            steno.error("Couldn't load configuration - will not be able to check firmware version");
        }

        firmwareLoadService.setOnSucceeded((WorkerStateEvent t) ->
        {
            FirmwareLoadResult result = (FirmwareLoadResult) t.getSource().getValue();
            boolean firmwareUpdatedOK = false;
            if (result.getStatus() == FirmwareLoadResult.SUCCESS)
            {
                BaseLookup.getSystemNotificationHandler().showFirmwareUpgradeStatusNotification(result);
//                try
//                {
//                    printerToUse.transmitUpdateFirmware(result.getFirmwareID());
                shutdown();
                firmwareUpdatedOK = true;
//                } catch (PrinterException ex)
//                {
//                }
            }

            if (!firmwareUpdatedOK)
            {
                BaseLookup.getSystemNotificationHandler().showFirmwareUpgradeStatusNotification(result);
            }
        });

        firmwareLoadService.setOnFailed((WorkerStateEvent t) ->
        {
            FirmwareLoadResult result = (FirmwareLoadResult) t.getSource().getValue();
            BaseLookup.getSystemNotificationHandler().showFirmwareUpgradeStatusNotification(result);
        });

        BaseLookup.getSystemNotificationHandler().configureFirmwareProgressDialog(firmwareLoadService);
    }

    @Override
    public void run()
    {
        while (keepRunning)
        {
            switch (commsState)
            {
                case FOUND:
                    steno.debug("Trying to connect to printer in " + printerHandle);

                    try
                    {
                        boolean printerCommsOpen = connectToPrinter();
                        if (printerCommsOpen)
                        {
                            steno.debug("Connected to Robox on " + printerHandle);
                            statusRequestCount = 0;
                            commsState = RoboxCommsState.CHECKING_FIRMWARE;
                        } else
                        {
                            steno.error("Failed to connect to Robox on " + printerHandle);
                            shutdown();
                        }
                    } catch (PortNotFoundException ex)
                    {
                        if (BaseConfiguration.getMachineType() == MachineType.WINDOWS)
                        {
                            steno.info("Port not ready for comms - windows needs time to settle...");
                        } else
                        {
                            steno.info("Port not ready for comms - non-windows - this is unusual");
                        }
                        shutdown();
                    }
                    break;

                case CHECKING_FIRMWARE:
                    steno.debug("Check firmware " + printerHandle);
                    if (loadingFirmware)
                    {
                        try
                        {
                            Thread.sleep(200);
                        } catch (InterruptedException ex)
                        {
                            steno.error("Interrupted while waiting for firmware to be loaded " + ex);
                        }
                        break;
                    }

                    FirmwareResponse firmwareResponse = null;
                    boolean loadRequiredFirmware = false;

                    try
                    {
                        firmwareResponse = printerToUse.readFirmwareVersion();
                        // Set the firmware version so that the system knows which response
                        // packet length to use.
                        firmwareVersionInUse = firmwareResponse.getFirmwareRevisionFloat();
                            
                        if (requiredFirmwareVersion > 0 && firmwareVersionInUse != requiredFirmwareVersion)
                        {
                            // The firmware version is different to that associated with AutoMaker
                            steno.warning(String.format("Firmware version is %.0f and should be %.0f.", firmwareVersionInUse, requiredFirmwareVersion));

                            // Check that the printer ID is valid, as updating the firmware with a corrupt printer ID
                            // can cause serious problems.
                            lastPrinterIDResponse = printerToUse.readPrinterID();
                            if (!lastPrinterIDResponse.isValid())
                            {
                                steno.warning("Printer does not have a valid ID!");
                                commsState = RoboxCommsState.RESETTING_ID;
                                break;
                            }

                            if (BaseConfiguration.isApplicationFeatureEnabled(ApplicationFeature.AUTO_UPDATE_FIRMWARE))
                            {
                                if (firmwareVersionInUse >= 691)
                                {
                                    // Is the SD card present?
                                    try
                                    {
                                        StatusRequest request = (StatusRequest) RoboxTxPacketFactory.createPacket(TxPacketTypeEnum.STATUS_REQUEST);
                                        StatusResponse response = (StatusResponse) writeToPrinter(request, true);
                                        if (!response.issdCardPresent())
                                        {
                                            steno.warning("SD Card not present");
                                            BaseLookup.getSystemNotificationHandler().processErrorPacketFromPrinter(FirmwareError.SD_CARD, printerToUse);
                                            shutdown();
                                            break;
                                        } else
                                        {
                                            BaseLookup.getSystemNotificationHandler().clearAllDialogsOnDisconnect();
                                        }
                                    } catch (RoboxCommsException ex)
                                    {
                                        steno.error("Failure during printer status request. " + ex.toString());
                                        break;
                                    }
                                }

                                if (firmwareVersionInUse < requiredFirmwareVersion)
                                {
                                    // Tell the user to update
                                    loadRequiredFirmware = BaseLookup.getSystemNotificationHandler()
                                            .askUserToUpdateFirmware(printerToUse);
                                } else 
                                {
                                    // If printer firmware is more than required, we ask the user if they are sure of the downgrade
                                    loadRequiredFirmware = BaseLookup.getSystemNotificationHandler()
                                            .showDowngradeFirmwareDialog(printerToUse);
                                }
                                
                            }
                        }

                        if (loadRequiredFirmware)
                        {
                            loadingFirmware = true;
                            loadFirmware(BaseConfiguration.getCommonApplicationDirectory()
                                    + "robox_r" + requiredFirmwareVersionString + ".bin");
                        } else
                        {
                            moveOnFromFirmwareCheck(firmwareResponse);
                        }
                    } catch (PrinterException ex)
                    {
                        steno.debug("Exception whilst checking firmware version: " + ex);
                        shutdown();
                    }
                    break;

                case CHECKING_ID:
                    steno.debug("Check id " + printerHandle);
                    try
                    {
                        // Printer ID may or may not have been read, so get it again.
                        lastPrinterIDResponse = printerToUse.readPrinterID();
                        if (!lastPrinterIDResponse.isValid())
                        {
                            steno.warning("Printer does not have a valid ID: " + lastPrinterIDResponse.toString());
                            commsState = RoboxCommsState.RESETTING_ID;
                            break;
                        }
                        
                        printerName = lastPrinterIDResponse.getPrinterFriendlyName();

                        if (printerName == null
                                || (printerName.length() > 0
                                && printerName.charAt(0) == '\0'))
                        {
                            steno.debug("Connected to unknown printer - setting to RBX01");
                            BaseLookup.getSystemNotificationHandler().
                                    showNoPrinterIDDialog(printerToUse);
                            printerName = PrinterContainer.defaultPrinterID;
                        } else
                        {
                            steno.debug("Connected to printer " + printerName);
                        }

                        PrinterDefinitionFile printerConfigFile = null;

                        if (lastPrinterIDResponse.getModel() != null)
                        {
                            printerConfigFile = PrinterContainer.getPrinterByID(lastPrinterIDResponse.getModel());
                        }

                        if (printerConfigFile == null)
                        {
                            printerConfigFile = PrinterContainer.getPrinterByID(PrinterContainer.defaultPrinterID);
                        }
                        printerToUse.setPrinterConfiguration(printerConfigFile);
                        for (PrinterEdition editionUnderExamination : printerConfigFile.getEditions())
                        {
                            if (editionUnderExamination.getTypeCode().equalsIgnoreCase(lastPrinterIDResponse.getEdition()))
                            {
                                printerToUse.setPrinterEdition(editionUnderExamination);
                                break;
                            }
                        }
                        if (!(this instanceof RoboxRemoteCommandInterface))
                            printerToUse.setAmbientLEDColour(Color.web(lastPrinterIDResponse.getPrinterColour()));
                    } catch (PrinterException ex)
                    {
                        steno.error("Error whilst checking printer ID");
                    }

                    commsState = RoboxCommsState.DETERMINING_PRINTER_STATUS;
                    break;

                case RESETTING_ID:
                    steno.debug("Resetting identity for " + printerHandle);
                    RoboxResetIDResult resetResult = RoboxResetIDResult.RESET_NOT_DONE;
                    if (BaseConfiguration.isApplicationFeatureEnabled(ApplicationFeature.RESET_PRINTER_ID))
                    {
                        resetResult = BaseLookup.getSystemNotificationHandler()
                                                .askUserToResetPrinterID(printerToUse, lastPrinterIDResponse);
                    }
                    switch (resetResult)
                    {
                        case RESET_SUCCESSFUL: // Reset of printer id successful
                            steno.debug("Reset ID of " + printerHandle);
                            commsState = RoboxCommsState.CHECKING_ID;
                            break;
                        case RESET_TEMPORARY: // Temporary set of printer type successful.
                            steno.debug("Set temporary identity for " + printerHandle);
                            commsState = RoboxCommsState.DETERMINING_PRINTER_STATUS;
                            break;
                        case RESET_NOT_DONE: // Not done - set a default.
                            steno.debug("Set default ID for " + printerHandle);
                            commsState = RoboxCommsState.DETERMINING_PRINTER_STATUS;
                            printerToUse.setPrinterConfiguration(PrinterContainer.getPrinterByID(PrinterContainer.defaultPrinterID));
                            break;
                        case RESET_CANCELLED:
                        case RESET_FAILED:
                        default: // Cancelled or failed. Disconnect printer.
                            steno.debug("Failed to set printer ID for " + printerHandle);
                            shutdown();
                            break;
                    }
                    break;
                    
                case DETERMINING_PRINTER_STATUS:
                    steno.debug("Determining printer status on port " + printerHandle);

                    try
                    {
                        StatusResponse statusResponse = (StatusResponse) writeToPrinter(
                                RoboxTxPacketFactory.createPacket(
                                        TxPacketTypeEnum.STATUS_REQUEST), true);

                        determinePrinterStatus(statusResponse);

                        steno.debug("Printer connected");
                        
                        controlInterface.printerConnected(printerHandle);

                        //Stash the connected printer info
                        String printerIDToUse = null;
                        if (lastPrinterIDResponse != null
                                && lastPrinterIDResponse.getAsFormattedString() != null)
                        {
                            printerIDToUse = lastPrinterIDResponse.getAsFormattedString();
                        }
                        CoreMemory.getInstance().setLastPrinterSerial(printerIDToUse);
                        CoreMemory.getInstance().setLastPrinterFirmwareVersion(firmwareVersionInUse);

                        commsState = RoboxCommsState.CONNECTED;
                    } catch (RoboxCommsException ex)
                    {
                        if (printerFriendlyName != null)
                        {
                            steno.error("Failed to determine printer status on "
                                    + printerFriendlyName);
                        } else
                        {
                            steno.error("Failed to determine printer status on unknown printer");
                        }
                        shutdown();
                    }

                    break;
                case CONNECTED:
                    try
                    {
                        if (!suspendStatusChecks && isConnected && commsState == RoboxCommsState.CONNECTED)
                        {
                            try
                            {
                                writeToPrinter(RoboxTxPacketFactory.createPacket(TxPacketTypeEnum.STATUS_REQUEST));
                                
                                writeToPrinter(RoboxTxPacketFactory.createPacket(TxPacketTypeEnum.REPORT_ERRORS));

                                // If we're talking to a remote printer we need to keep checking data that may have changed without us knowing
                                if (this instanceof RoboxRemoteCommandInterface)
                                {
                                    writeToPrinter(RoboxTxPacketFactory.createPacket(TxPacketTypeEnum.READ_PRINTER_ID));
                                }
                                statusRequestCount = 0;
                            } catch (RoboxCommsException ex)
                            {
                                if (isConnected)
                                {
                                    // Disconnect printer after max allowed number of failed attempts.
                                    ++statusRequestCount;
                                    if (statusRequestCount > maxAllowedStatusRequestCount)
                                    {
                                        steno.warning("Failure during printer status request: " + ex);
                                        //steno.debug("Status request count for " + printerHandle.getConnectionHandle() + " (" + Integer.toString(statusRequestCount) + ") exceeds maxAllowedStatusRequestCount(" + Integer.toString(maxAllowedStatusRequestCount));
                                        shutdown();
                                    }
                                }
                            }
                        }

                        this.sleep(sleepBetweenStatusChecks);
                    } catch (InterruptedException ex)
                    {
                        steno.debug("Comms interrupted");
                    }
                    
                    break;

                case DISCONNECTED:
                    steno.debug("state is disconnected");
                    break;
                default:
                    break;
            }
        }
        steno.info("Handler for " + printerHandle.getConnectionHandle() + " beginning exit routine - state was " + commsState);
        finalShutdown();
        steno.info("Handler for " + printerHandle.getConnectionHandle() + " exited");
    }

    private void moveOnFromFirmwareCheck(FirmwareResponse firmwareResponse)
    {
        if (suppressPrinterIDChecks == false)
        {
            commsState = RoboxCommsState.CHECKING_ID;
        } else
        {
            commsState = RoboxCommsState.DETERMINING_PRINTER_STATUS;
        }
        loadingFirmware = false;
    }

    private void suspendStatusChecks(boolean suspendStatusChecks)
    {
        this.suspendStatusChecks = suspendStatusChecks;
    }

    public void loadFirmware(String firmwareFilePath)
    {
        steno.debug("Being asked to load firmware - status is " + commsState + " thread " + this.getName());
        suspendStatusChecks(true);
//        this.interrupt();
        firmwareLoadService.reset();
        firmwareLoadService.setPrinterToUse(printerToUse);
        firmwareLoadService.setFirmwareFileToLoad(firmwareFilePath);
        firmwareLoadService.start();
    }

    public void shutdown()
    {
        keepRunning = false;
        commsState = RoboxCommsState.SHUTTING_DOWN;
    }

    private void finalShutdown()
    {
        steno.debug("Shutdown command interface...");
        keepRunning = false;
        commsState = RoboxCommsState.SHUTTING_DOWN;
        suspendStatusChecks(true);
        disconnectPrinterImpl();
        Platform.runLater(() ->
        {
            if (firmwareLoadService.isRunning())
            {
                steno.info("Shutdown command interface firmware service...");
                firmwareLoadService.cancel();
            }
        });
        steno.debug("set state to disconnected");
        commsState = RoboxCommsState.DISCONNECTED;
        isConnected = false;
        asyncWriteThread.shutdown();
        steno.debug("Shutdown command interface for " + printerHandle.getConnectionHandle() + " complete");
        controlInterface.disconnected(printerHandle);
    }

    /**
     *
     * @param sleepMillis
     */
    protected abstract void setSleepBetweenStatusChecks(int sleepMillis);

    /**
     *
     * @param messageToWrite
     * @return
     * @throws RoboxCommsException
     */
    public final RoboxRxPacket writeToPrinter(RoboxTxPacket messageToWrite) throws RoboxCommsException
    {
        if (isConnected)
        {
            return writeToPrinter(messageToWrite, false);
        } else
        {
            return null;
        }
    }

    /**
     *
     * @param messageToWrite
     * @param dontPublishResult
     * @return
     * @throws RoboxCommsException
     */
    public final RoboxRxPacket writeToPrinter(RoboxTxPacket messageToWrite, boolean dontPublishResult) throws RoboxCommsException
    {
        if (isConnected)
        {
            return asyncWriteThread.sendCommand(new CommandPacket(messageToWrite, dontPublishResult));
        } else
        {
            return null;
        }
    }

    public abstract RoboxRxPacket writeToPrinterImpl(RoboxTxPacket messageToWrite,
            boolean dontPublishResult) throws RoboxCommsException;

    /**
     *
     * @param printer
     */
    public void setPrinter(Printer printer)
    {
        // The asyncWriterThread is started here because it is potentially risky
        // starting a thread in the constructor, as the command interface is not
        // properly initialised. As not much can happen until a printer is set,
        // it is started here. 
        this.printerToUse = printer;
        State s = asyncWriteThread.getState();
        
        asyncWriteThread.start();
    }

    /**
     *
     * @return
     */
    public final boolean connectToPrinter() throws PortNotFoundException
    {
        isConnected = connectToPrinterImpl();
        return isConnected;
    }

    /**
     *
     * @return @throws celtech.roboxbase.comms.exceptions.PortNotFoundException
     */
    protected abstract boolean connectToPrinterImpl() throws PortNotFoundException;

    /**
     *
     */
    protected abstract void disconnectPrinterImpl();

    private void determinePrinterStatus(StatusResponse statusResponse)
    {
        if (statusResponse != null)
        {
            if (PrinterUtils.printJobIDIndicatesPrinting(statusResponse.getRunningPrintJobID()))
            {
                if (printerFriendlyName != null)
                {
                    steno.debug(printerFriendlyName + " is printing");
                } else
                {
                    steno.error("Connected to an unknown printer that is printing");
                }

            }
        }
    }

    public void operateRemotely(boolean enableRemoteOperation)
    {
        suspendStatusChecks(enableRemoteOperation);
    }

    public boolean isLocalPrinter()
    {
        return localPrinter;
    }

    public DetectedDevice getPrinterHandle()
    {
        return printerHandle;
    }
    
    public void clearAllErrors()
    {
        // Nothing to do by default.
    }

    public void clearError(FirmwareError error)
    {
        // Nothing to do by default.
    }
}
