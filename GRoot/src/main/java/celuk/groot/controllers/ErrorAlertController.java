package celuk.groot.controllers;

import celuk.groot.remote.ErrorDetails;
import celuk.groot.remote.RootPrinter;
import celuk.groot.remote.RootServer;
import celuk.language.I18n;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Tony
 */
public class ErrorAlertController {
    private class AlertData {
        public RootPrinter printer;
        public ErrorDetails errorData;
        
        public AlertData(RootPrinter printer, ErrorDetails errorData) {
            this.printer = printer;
            this.errorData = errorData;
        }
    };
    
    private final Map<String, ChangeListener<Boolean>> listenerMap = new HashMap<>();
    private final RootServer server;
    private String abortText = "error.abort";
    private String clearText = "error.clear";
    private String continueText = "error.continue";
    private String dialogTitleText = "error.dialogTitle";
    private String retryText = "error.retry";
    private String ejectFilament1Text = "error.ejectFilament1";
    private String ejectFilament2Text = "error.ejectFilament2";
    private final Alert errorAlert = new Alert(Alert.AlertType.NONE); 
    private final ChangeListener<Boolean> printerMapHeartbeatListener;
    
    public ErrorAlertController(RootServer server) {
        this.server = server;
        printerMapHeartbeatListener = (pr, ov, nv) -> {
            //System.out.println("");
            processPrinterMap(server.getCurrentPrinterMap());
        };
    }
    
    public void prepareDialog() {
        abortText = I18n.t(abortText);
        clearText = I18n.t(clearText);
        continueText = I18n.t(continueText);
        retryText = I18n.t(retryText);
        ejectFilament1Text = I18n.t(ejectFilament1Text);
        ejectFilament2Text = I18n.t(ejectFilament2Text);
        dialogTitleText = I18n.t(dialogTitleText);
        server.getCurrentPrinterMapHeartbeatProperty().addListener(printerMapHeartbeatListener);
        processPrinterMap(server.getCurrentPrinterMap());
        errorAlert.setOnHidden(e -> {
            handleAlertResponse();
        });
    }
    
    public RootServer getServer() {
        return server;
    }
    
    private void processPrinterMap(Map<? extends String, ? extends RootPrinter> printerMap) {
        List<String> lostPrinters = listenerMap.keySet()
                                               .stream()
                                               .filter(pid -> !printerMap.containsKey(pid))
                                               .collect(Collectors.toList());
        
        lostPrinters.forEach(pid -> {
            var p = printerMap.get(pid);
            if (p != null) {
                var l = listenerMap.get(pid);
                p.getActiveErrorHeartbeatProperty().removeListener(l);
                listenerMap.remove(pid);
            }
        });
        
        // Get a list of printers that are in the printer map, but do not have error listeners.        
        List<String> foundPrinters = printerMap.keySet()
                                               .stream()
                                               .filter(pid -> !listenerMap.containsKey(pid))
                                               .collect(Collectors.toList());
        foundPrinters.forEach(pid -> {
            RootPrinter printer = printerMap.get(pid);
            //System.out.println("Adding printer " + p.getPrinterName());
            ChangeListener<Boolean> l = (pr, ov, nv) -> {
                displayError(printer);
            };
            printer.getActiveErrorHeartbeatProperty().addListener(l);
            listenerMap.put(pid, l);
        });
    }
    
    private void displayError(RootPrinter printer) {
        Map<Integer, ErrorDetails> errorMap = printer.getActiveErrorMapProperty().get();
        //errorMap.forEach((errorCode, error) -> {
        //    System.err.println("Error code " 
        //                       + Integer.toString(errorCode)
        //                       + " - "
        //                       + error.getErrorTitle());
        //});
        if (errorMap != null) {
            Platform.runLater(() -> {
                handleError(printer, errorMap);
            });
        }
    }

    private void handleError(RootPrinter printer, Map<Integer, ErrorDetails> errorMap) {
        if (errorAlert.isShowing()) {
            AlertData aData = (AlertData)errorAlert.getDialogPane().getUserData();
            if (aData != null && aData.printer.getPrinterId().equals(printer.getPrinterId())) {
                if (errorMap == null || errorMap.isEmpty() || !errorMap.containsKey(aData.errorData.getErrorCode())) {
                    errorAlert.hide();
                    errorAlert.getDialogPane().setUserData(null);
               }
            }
        }
        if (errorMap != null && !errorMap.isEmpty() && !errorAlert.isShowing()) {
            // Select the highest priority error.
            String printerName = printer.getPrinterName();

            Map.Entry<Integer, ErrorDetails> errorEntry = errorMap.entrySet()
                .stream()
                .reduce(null, (a, b) -> (a != null && a.getKey() < b.getKey() ? a : b));

            int errorCode = errorEntry.getKey();
            ErrorDetails errorData = errorEntry.getValue();

            // Error needs to be raised for the user
            // Remove HTML tags from error message.
            String errorMessage = errorData.getErrorMessage().replaceAll("<([^>]*)>", "");
            errorAlert.setHeaderText(errorData.getErrorTitle());
            errorAlert.setContentText(errorMessage);
            errorAlert.setTitle(dialogTitleText.replace("#1", printerName));
            AlertData aData = new AlertData(printer, errorData);
            errorAlert.getDialogPane().setUserData(aData);
            List<ButtonType> buttonList = errorAlert.getButtonTypes();
            buttonList.clear();
            int options = errorData.getOptions();
            // ABORT(1),
            // CLEAR_CONTINUE(2),
            // RETRY(4),
            // OK(8),
            // OK_ABORT(16),
            // OK_CONTINUE(32);
            if ((options & 17) != 0) { // ABORT or OK_ABORT
                buttonList.add(ButtonType.CANCEL);
                Button b = (Button)errorAlert.getDialogPane().lookupButton(ButtonType.CANCEL);
                b.setText(abortText);
            }

            if (options == 0 || // Nothing or
                (options & 46) != 0) { // CLEAR_CONTINUE or RETRY or OK or OK_CONTINUE.
                String buttonText = ((options & 4) != 0 ? retryText : continueText);
                buttonList.add(ButtonType.OK);
                Button b = (Button)errorAlert.getDialogPane().lookupButton(ButtonType.OK);
                b.setText(buttonText);
            }

            if (errorCode == 28) { // E_UNLOAD_ERROR
                buttonList.add(ButtonType.APPLY);
                Button b = (Button)errorAlert.getDialogPane().lookupButton(ButtonType.APPLY);
                b.setText(ejectFilament1Text);
            }
            else if (errorCode == 29) { // D_UNLOAD_ERROR
                // D_UNLOAD_ERROR
                buttonList.add(ButtonType.APPLY);
                Button b = (Button)errorAlert.getDialogPane().lookupButton(ButtonType.APPLY);
                b.setText(ejectFilament2Text);
            }

            errorAlert.show();
        }
    }

    private void handleAlertResponse() {
        AlertData aData = (AlertData)errorAlert.getDialogPane().getUserData();
        ButtonType result = errorAlert.getResult();
        if (aData != null && result != null) {
            String typeCode = result.getButtonData().getTypeCode();
            //System.out.println("Returned " + typeCode);
            switch(typeCode) {
                case "O":
                {
                    // OK/Continue button.
                    //System.out.println("OK");
                    server.runBackgroundTask(() -> {
                        if ((aData.errorData.getOptions() & 38) != 0) { // CLEAR_CONTINUE or RETRY or OK_CONTINUE.
                            aData.printer.runResumeTask();
                        }
                        aData.printer.runClearErrorTask(aData.errorData.getErrorCode());
                        return null;
                    });
                    break;
                }

                case "A":
                {
                    // Apply/Eject button.
                    //System.out.println("Eject");
                    server.runBackgroundTask(() -> {
                        aData.printer.runEjectFilamentTask(aData.errorData.getErrorCode() == 28 ? 1 : 0);
                        aData.printer.runClearErrorTask(aData.errorData.getErrorCode());
                        return null;
                    });
                    break;
                }

                case "C":
                {
                    // Cancel/Abort button.
                    //System.out.println("Abort");
                    server.runBackgroundTask(() -> {
                        aData.printer.runCancelTask();
                        aData.printer.runClearErrorTask(aData.errorData.getErrorCode());
                        return null;
                    });
                    break;
                }

                default:
                    break;
            }
        }
        aData.printer.acknowledgeError(aData.errorData);
    }
}
