package celuk.groot.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class RootPrinter extends Updater {
    
    private static final String CANCEL_COMMAND = "/remoteControl/cancel";
    private static final String COMMAND_PREFIX = "/api/";
    private static final String CHANGE_PRINTER_COLOUR_COMMAND = "/remoteControl/changePrinterColour";
    private static final String CLEAN_NOZZLE_COMMAND = "/remoteControl/cleanNozzle";
    private static final String CLEAR_ERROR_COMMAND = "/remoteControl/clearError";
    private static final String EJECT_FILAMENT_COMMAND = "/remoteControl/ejectFilament";
    private static final String EJECT_STUCK_MATERIAL_COMMAND = "/remoteControl/ejectStuckMaterial";
    private static final String ERROR_STATUS_COMMAND = "/remoteControl/activeErrorStatus";
    private static final String EXECUTE_GCODE_COMMAND = "/remoteControl/executeGCode";
    private static final String HEAD_EEPROM_COMMAND = "/remoteControl/headEEPROM";
    private static final String LIST_REPRINTABLE_JOBS_COMMAND = "/remoteControl/listReprintableJobs";
    private static final String LIST_USB_PRINTABLE_JOBS_COMMAND = "/remoteControl/listUSBPrintableJobs";
    private static final String MATERIAL_STATUS_COMMAND = "/remoteControl/materialStatus";
    private static final String MACRO_COMMAND = "/remoteControl/runMacro";
    private static final String PAUSE_COMMAND = "/remoteControl/pause";
    private static final String PRINT_ADJUST_COMMAND = "/remoteControl/printAdjust";
    private static final String PRINT_USB_JOB_COMMAND = "/remoteControl/printUSBJob";
    private static final String PURGE_TO_TARGET_COMMAND = "/remoteControl/purgeToTarget";
    private static final String RENAME_PRINTER_COMMAND = "/remoteControl/renamePrinter";
    private static final String REMOTE_CONTROL_COMMAND = "/remoteControl";
    private static final String REMOVE_HEAD_COMMAND = "/remoteControl/removeHead";
    private static final String REPRINT_JOB_COMMAND = "/remoteControl/reprintJob";
    private static final String RESUME_COMMAND = "/remoteControl/resume";
    private static final String SET_HEAD_EEPROM_COMMAND = "/remoteControl/setHeadEEPROM";
    private static final String SET_PRINT_ADJUST_COMMAND = "/remoteControl/setPrintAdjust";
    private static final String SWITCH_AMBIENT_LIGHT_COMMAND = "/remoteControl/setAmbientLED";
    private static final String TIDY_PRINT_JOB_DIRS_COMMAND = "/remoteControl/tidyPrintJobDirs";
    
    private final RootServer rootServer;
    private final String printerId;
    private final SimpleObjectProperty<PrinterStatusResponse> currentStatusProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<PrintAdjustData> currentPrintAdjustDataProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<HeadEEPROMData> currentHeadEEPROMDataProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<MaterialStatusData> currentMaterialStatusDataProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<PurgeData> currentPurgeDataProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Map<Integer, ErrorDetails>> activeErrorMapProperty = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty activeErrorHeartbeatProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty hasActiveErrorProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty safetiesOnProperty = new SimpleBooleanProperty(false);
    private Set<Integer> acknowledgedErrorSet = new HashSet<>();
    
    private ErrorDetails fakeError = null;
    private int fakeCount = 0;
    
    public RootPrinter(RootServer rootServer, String printerId) {
        super();
        this.rootServer = rootServer;
        this.printerId = printerId;
    }
    
    public String getPrinterId() {
        return printerId;
    }
    
    public String getPrinterName() {
        PrinterStatusResponse sr = currentStatusProperty.get();
        if (sr != null)
            return sr.getPrinterName();
        else
            return "-?-";
    }
    
    public String getPrinterTypeCode() {
        PrinterStatusResponse sr = currentStatusProperty.get();
        if (sr != null)
            return sr.getPrinterTypeCode();
        else
            return "";
    }

    public SimpleObjectProperty<PrinterStatusResponse> getCurrentStatusProperty() {
        return currentStatusProperty;
    }
    
    public SimpleObjectProperty<PrintAdjustData> getCurrentPrintAdjustDataProperty() {
        return currentPrintAdjustDataProperty;
    }

    public SimpleObjectProperty<HeadEEPROMData> getCurrentHeadEEPROMDataProperty() {
        return currentHeadEEPROMDataProperty;
    }

    public SimpleObjectProperty<MaterialStatusData> getCurrentMaterialStatusDataProperty() {
        return currentMaterialStatusDataProperty;
    }

    public SimpleObjectProperty<PurgeData> getCurrentPurgeDataProperty() {
        return currentPurgeDataProperty;
    }

    public SimpleBooleanProperty getSafetiesOnProperty() {
        return safetiesOnProperty;
    }

    public SimpleBooleanProperty getActiveErrorHeartbeatProperty() {
        return activeErrorHeartbeatProperty;
    }
    
    public SimpleBooleanProperty getHasActiveErrorProperty() {
        return hasActiveErrorProperty;
    }

    public SimpleObjectProperty<Map<Integer, ErrorDetails>> getActiveErrorMapProperty() {
        return activeErrorMapProperty;
    }

    public void acknowledgeError(ErrorDetails error) {
        acknowledgedErrorSet.add(error.getErrorCode());
    }

    public RootServer getRootServer() {
        return rootServer;
    }

    public Future<PrinterStatusResponse> runRequestPrinterStatusTask() {
        //System.out.println("Requesting status of printer \"" + printerId + "\"");
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + REMOTE_CONTROL_COMMAND, true, RootServer.READ_TIMEOUT_SHORT, null,
            (var requestData, var jMapper) -> {
                PrinterStatusResponse statusResponse = null;
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating printer status of \"" + printerId + "\" - \"" + new String(requestData) + "\"");
                        statusResponse = jMapper.readValue(requestData, PrinterStatusResponse.class);
                        currentStatusProperty.set(statusResponse);
                        if (statusResponse.getPrinterStatusEnumValue().startsWith("PRINTING_PROJECT")
                            || statusResponse.getPrinterStatusEnumValue().startsWith("RUNNING_MACRO")
                            || statusResponse.getPrinterStatusEnumValue().startsWith("PAUSED")
                            || statusResponse.getPrinterStatusEnumValue().startsWith("SELFIE_PAUSE")
                            || statusResponse.getPrinterStatusEnumValue().startsWith("PAUSE_PENDING")
                            || statusResponse.getPrinterStatusEnumValue().startsWith("RESUME_PENDING")
                            || statusResponse.getPrinterStatusEnumValue().startsWith("HEATING"))
                        {
                            runRequestPrintAdjustDataTask();
                        }
                        else
                        {
                            // There is a race condition between this and any active requestPrintAdjustDataTask
                            // which could result in out-of-date data being available. I don't think this matters
                            // and should be cleared by the next update.
                            currentPrintAdjustDataProperty.set(null);
                        }
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding printer status response from @" 
                                       + rootServer.getHostAddress()
                                       + ":" 
                                       + rootServer.getHostPort() 
                                       + " - " 
                                       + ex.getMessage());
                }
                return statusResponse;
            },
            null);
    }
    
    private Future<PrintAdjustData> runRequestPrintAdjustDataTask() {
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + PRINT_ADJUST_COMMAND, true, RootServer.READ_TIMEOUT_SHORT, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                PrintAdjustData adjustData = null;
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating print adjust data of \"" + getPrinterId() + "\" - \"" + new String(requestData) + "\"");
                        adjustData = jMapper.readValue(requestData, PrintAdjustData.class);
                        //System.out.println("Updating print adjust data of \"" + getPrinterId() + "\" - target bed temp = " + Double.toString(adjustData.getBedTargetTemp()));
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding print adjust data from @" 
                                       + getRootServer().getHostAddress() 
                                       + ":" + getRootServer().getHostPort()
                                       + " - " 
                                       + ex.getMessage());
                }
                currentPrintAdjustDataProperty.set(adjustData);
                return adjustData;
            },
            null);
    }

    private void processErrorList(List<ErrorDetails> errorList) {
        Map<Integer, ErrorDetails> activeMap = new HashMap<>();
        Set<Integer> ackSet = new HashSet<>();
        if (errorList != null) {
            errorList.forEach(e -> {
                int errorCode = e.getErrorCode();
                if (errorCode == 25 || // B_POSITION_LOST
                    errorCode == 33 || // B_POSITION_WARNING
                    errorCode == 41) { // BED_TEMPERATURE_DROOP
                    // Ignore these errors for now. Note that they are not cleared,
                    // in case another app wants to display them.
                }
                else {
                    if (acknowledgedErrorSet.contains(e.getErrorCode()))
                        ackSet.add(e.getErrorCode());
                    else
                        activeMap.put(e.getErrorCode(), e);
                }
            });
        }
        
        acknowledgedErrorSet = ackSet;
        activeErrorMapProperty.setValue(activeMap);
        hasActiveErrorProperty.set(!activeMap.isEmpty());
        // The original idea was to have a listener on the activeErrorMapProperty, which was to be called every
        // time the map is updated with a new map. Unfortunately, this doesn't work because the listener is not
        // called when the new map is not the same instance, but is equal to the old one.
        // So instead we have a heartbeat property that is toggled every time the map is updated.
        activeErrorHeartbeatProperty.set(!activeErrorHeartbeatProperty.get());
        //if (!activeMap.isEmpty()) {
            //System.err.println("Errors on printer \"" 
            //                   + currentStatusProperty.get().getPrinterName()
            //                   + "\"");
            //activeMap.forEach((ec, e) -> {
            //    System.err.println("    Error "
            //                       + Integer.toString(ec)
            //                       + ": " + e.getErrorTitle()
            //                       + " - " 
            //                       + e.getErrorMessage());
            //});
        //}
    }

    private Future<ActiveErrorStatusData> runRequestErrorStatusTask() {
        //System.out.println("Requesting error status of printer \"" + printerId + "\"");
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + ERROR_STATUS_COMMAND, true, RootServer.READ_TIMEOUT_SHORT, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                ActiveErrorStatusData activeErrorData = null;
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating error status of \"" + getPrinterId() + "\" - \"" + new String(requestData) + "\"");
                        activeErrorData = jMapper.readValue(requestData, ActiveErrorStatusData.class);
                        processErrorList(activeErrorData.getActiveErrors());
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding error status of @"
                                       + getRootServer().getHostAddress() 
                                       + ":" + getRootServer().getHostPort() 
                                       + " - " 
                                       + ex.getMessage());
                }
                return activeErrorData;
            },
            null);
    }

    @Override
    protected void update() {
        runRequestPrinterStatusTask();
        runRequestErrorStatusTask();
    }
    
    public Future<Boolean> runEjectFilamentTask(int filamentNumber) {
        String f = Integer.toString(filamentNumber);
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + EJECT_FILAMENT_COMMAND,
            false,
            RootServer.READ_TIMEOUT_SHORT,
            f,
            (byte[] requestData, ObjectMapper jMapper) -> {
                return true;
            },
            null);
    }
    
    public Future<String> runSendGCodeTask(String gCode) {
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + EXECUTE_GCODE_COMMAND,
            false,
            RootServer.READ_TIMEOUT_SHORT,
            gCode,
            (byte[] requestData, ObjectMapper jMapper) -> {
                String response = "";

                try {
                    if (requestData.length > 0) {
                        String[] r = jMapper.readValue(requestData, String[].class);
                        if (r.length > 0 && r[0] != null)
                            response = r[0].trim();
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error when executing GCode command \""
                                        + gCode
                                        + "\" on @"
                                        + rootServer.getHostAddress()
                                        + ":"
                                        + rootServer.getHostPort()
                                        + " - "
                                        + ex.getMessage());
                }
                return response;
            },
            null);
    }

    public Future<String> runUnlockDoorTask() {
        return runSendGCodeTask("G37 S");
    }

    private Future<Void> runVoidTask(String command, String data) {
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + command,
            false,
            RootServer.READ_TIMEOUT_SHORT,
            data,
            (byte[] requestData, ObjectMapper jMapper) -> {
                runRequestPrinterStatusTask();
                return null;
            },
            null);
    }

    public Future<Void> runPauseTask() {
        return runVoidTask(PAUSE_COMMAND, null);
    }
    
    public Future<Void> runResumeTask() {
        return runVoidTask(RESUME_COMMAND, null);
    }

    public Future<Void> runCancelTask() {
        return runVoidTask(CANCEL_COMMAND, safetiesOnProperty.get() ? "\"true\""
                                                                       : "\"false\"");
    }

    public Future<Void> runMacroTask(String macro) {
        return runVoidTask(MACRO_COMMAND, macro);
    }

    public Future<Void> runSwitchAmbientLightTask(String state) {
        String data = String.format("\"%s\"", state);
        return runVoidTask(SWITCH_AMBIENT_LIGHT_COMMAND, state);
    }

    public Future<Void> runSetPrintAdjustDataTask(String data) {
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + SET_PRINT_ADJUST_COMMAND,
            false,
            RootServer.READ_TIMEOUT_SHORT,
            data,
            (byte[] requestData, ObjectMapper jMapper) -> {
                // Wait for update to print adjust data.
                try {
                    //System.out.println("Setting print adjust data of \"" + getPrinterId() + "\" - \"" + data + "\"");
                    runRequestPrintAdjustDataTask().get();
                }
                catch (InterruptedException  ex) {
                    System.err.println("Interrupted exception during request for print adjust data");
                }
                catch (ExecutionException ex) {
                    System.err.println("Execution exception during request for print adjust data");
                }
                return null;
            },
            null);
    }
    
    public Future<Void> runRenamePrinterTask(String printerName) {
        String data = String.format("\"%s\"", printerName);
        return runVoidTask(RENAME_PRINTER_COMMAND, data);
    }

    public Future<Void> runReprintJobTask(String printJobID) {
        return runVoidTask(REPRINT_JOB_COMMAND, printJobID);
    }
    
    public Future<Void> runPrintUSBJobTask(String printJobID, String printJobPath) {
        String data = String.format("{\"printJobID\":\"%s\",\"printJobPath\":\"%s\"}", printJobID, printJobPath);
        return runVoidTask(PRINT_USB_JOB_COMMAND, data);
    }

    public Future<Void> runRemoveHeadTask() {
        return runVoidTask(REMOVE_HEAD_COMMAND, safetiesOnProperty.get() ? "\"true\""
                                                                         : "\"false\"");
    }

    public Future<Void> runChangePrinterColourTask(String printerColour) {
        String data = String.format("\"%s\"", printerColour);
        return runVoidTask(CHANGE_PRINTER_COLOUR_COMMAND, data);
    }

    public Future<Void> runCleanNozzleTask(int nozzleNumber) {
        String data = String.format("\"%d\"", nozzleNumber);
        return runVoidTask(CLEAN_NOZZLE_COMMAND, data);
    }

    public Future<Void> runClearErrorTask(int errorCode) {
        String data = String.format("\"%d\"", errorCode);
        return runVoidTask(CLEAR_ERROR_COMMAND, data);
    }

    public Future<Void> runEjectStuckMaterialTask(int materialNumber) {
        String data = String.format("\"%d\"", materialNumber);
        return runVoidTask(EJECT_STUCK_MATERIAL_COMMAND, data);
    }

    public Future<PrintJobListData> runListPrintableJobsTask(boolean reprintableMode) {
        String command = reprintableMode ? LIST_REPRINTABLE_JOBS_COMMAND : LIST_USB_PRINTABLE_JOBS_COMMAND;
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + command,
            false,
            RootServer.READ_TIMEOUT_LONG,
            null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                PrintJobListData printJobList = null;
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Listing printable jobs from \"" + getPrinterId() + "\" - \"" + new String(requestData) + "\"");
                        printJobList = jMapper.readValue(requestData, PrintJobListData.class);
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding print job list from @"
                                       + getRootServer().getHostAddress()
                                       + ":"
                                       + getRootServer().getHostPort()
                                       + " - "
                                       + ex.getMessage());
                }
                return printJobList;
            },
            null);
    }
    
    public Future<HeadEEPROMData> runRequestHeadEEPROMDataTask() {
        //System.out.println("Requesting head EEPROM data from printer \"" + printerId + "\"");
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + HEAD_EEPROM_COMMAND, true, RootServer.READ_TIMEOUT_SHORT, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                HeadEEPROMData headData = null;
                try {
                    if (requestData.length > 0) {
                        // System.out.println("Updating print adjust data of \"" + getPrinterId() + "\" - \"" + new String(requestData) + "\"");
                        headData = jMapper.readValue(requestData, HeadEEPROMData.class);
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding print adjust data from @" 
                                       + getRootServer().getHostAddress() 
                                       + ":" + getRootServer().getHostPort()
                                       + " - " 
                                       + ex.getMessage());
                }
                currentHeadEEPROMDataProperty.set(headData);
                return headData;
            }, null);
    }
    
    public Future<Void> runSetHeadEEPROMDataTask(HeadEEPROMData headData) {
        //System.out.println("Setting head EEPROM data from printer \"" + printerId + "\"");
        Future<Void> f;
        try {
            String mappedData = rootServer.getMapper().writeValueAsString(headData);
            f = runVoidTask(SET_HEAD_EEPROM_COMMAND, mappedData);
        } 
        catch (JsonProcessingException ex) {
            f = new CompletableFuture<>();
            ((CompletableFuture)f).completeExceptionally(ex);
        }
        return f;
    }

    public Future<Void> runTidyPrintJobDirsTask() {
        return runVoidTask(TIDY_PRINT_JOB_DIRS_COMMAND, null);
    }

    public Future<MaterialStatusData> runRequestMaterialStatusTask() {
        //System.out.println("Requesting material status from printer \"" + printerId + "\"");
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + MATERIAL_STATUS_COMMAND, true, RootServer.READ_TIMEOUT_SHORT, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                MaterialStatusData materialStatus = null;
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating print adjust data of \"" + getPrinterId() + "\" - \"" + new String(requestData) + "\"");
                        materialStatus = jMapper.readValue(requestData, MaterialStatusData.class);
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding print adjust data from @" 
                                       + getRootServer().getHostAddress() 
                                       + ":" + getRootServer().getHostPort()
                                       + " - " 
                                       + ex.getMessage());
                }
                currentMaterialStatusDataProperty.set(materialStatus);
                return materialStatus;
            },
            null);
    }

    public Future<PurgeData> runRequestPurgeDataTask() {
        Future<HeadEEPROMData> headFuture = runRequestHeadEEPROMDataTask();
        Future<MaterialStatusData> materialFuture = runRequestMaterialStatusTask();
        PurgeData pData = new PurgeData();
        CompletableFuture<PurgeData> f = new CompletableFuture<>();
        try {
            pData.setMaterialStatus(materialFuture.get());
            pData.setHeadData(headFuture.get());
            currentPurgeDataProperty.set(pData);
            f.complete(pData); 
        } catch (InterruptedException | ExecutionException ex) {
            f.completeExceptionally(ex);
        }

        return f;
    }
    
    public Future<Void> runPurgeTask(PurgeTarget targetData) {
        //System.out.println("Running purge on printer \"" + printerId + "\"");
        Future<Void> f;
        try {
            String mappedData = rootServer.getMapper().writeValueAsString(targetData);
            f = runVoidTask(PURGE_TO_TARGET_COMMAND, mappedData);
        } 
        catch (JsonProcessingException ex) {
            f = new CompletableFuture<>();
            ((CompletableFuture)f).completeExceptionally(ex);
        }
        return f;
    }
}
