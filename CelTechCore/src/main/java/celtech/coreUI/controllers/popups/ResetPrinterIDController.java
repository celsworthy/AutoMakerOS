package celtech.coreUI.controllers.popups;

import celtech.Lookup;
import celtech.coreUI.components.HyperlinkedLabel;
import celtech.coreUI.components.RestrictedTextField;
import celtech.roboxbase.comms.RoboxResetIDResult;
import celtech.roboxbase.comms.rx.PrinterIDResponse;
import celtech.roboxbase.configuration.datafileaccessors.PrinterContainer;
import celtech.roboxbase.configuration.fileRepresentation.PrinterDefinitionFile;
import celtech.roboxbase.configuration.fileRepresentation.PrinterEdition;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import celtech.roboxbase.printerControl.model.PrinterIdentity;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author Ian
 */
public class ResetPrinterIDController implements Initializable
{
    private static final String KEY_TO_THE_CRYPT = "4304504C02D05504B05204F04204F058";
    
    private RoboxResetIDResult resetResult = RoboxResetIDResult.RESET_NOT_DONE;
    
    private final Stenographer steno = StenographerFactory.getStenographer(ResetPrinterIDController.class.getName());

    private Printer printerToUse = null;
 
    private BooleanProperty identityValid = new SimpleBooleanProperty(false);
    
    @FXML
    private VBox mainVBox;

    @FXML
    private HyperlinkedLabel resetInstructionLabel;
    
    @FXML
    private RadioButton permSetRadioButton;

    @FXML
    private Label printerResetCodeLabel;

    @FXML
    private Label printerIDLabel;

    @FXML
    private TextField printerIDCodeField;
        
    @FXML
    private RestrictedTextField printerTypeCodeField;

    @FXML
    private RestrictedTextField printerEditionField;

    @FXML
    private RestrictedTextField printerWeekField;

    @FXML
    private RestrictedTextField printerYearField;

    @FXML
    private RestrictedTextField printerPONumberField;

    @FXML
    private RestrictedTextField printerSerialNumberField;

    @FXML
    private RestrictedTextField printerChecksumField;

    @FXML
    private RestrictedTextField printerElectronicsVersionField;

    @FXML
    private RadioButton tempSetRadioButton;

    @FXML
    private Label printerTypeLabel;

    @FXML
    private ChoiceBox<PrinterDefinitionFile> printerTypeChoice;
    
    @FXML
    private ChoiceBox<PrinterEdition> printerEditionChoice;

    @FXML
    private Button resetButton;

    @FXML
    private void resetPrinterID()
    {
        resetResult = RoboxResetIDResult.RESET_NOT_DONE;
        
        if (printerToUse != null)
        {
            if (tempSetRadioButton.isSelected())
            {
                printerToUse.setPrinterConfiguration(printerTypeChoice.getValue());
                printerToUse.setPrinterEdition(printerEditionChoice.getValue());
                resetResult = RoboxResetIDResult.RESET_TEMPORARY;
            }
            else if (permSetRadioButton.isSelected())
            {
                PrinterIdentity  newIdentity = decryptIDCode(printerIDCodeField.getText().trim());
                if (newIdentity != null)
                {
                    PrinterDefinitionFile printerConfigFile = PrinterContainer.getPrinterByID(newIdentity.printermodelProperty().get());
                    if (printerConfigFile != null)
                    {
                        printerToUse.setPrinterConfiguration(printerConfigFile);
                        printerConfigFile.getEditions().stream()
                                                       .filter(configEdition -> configEdition.getTypeCode().equalsIgnoreCase(newIdentity.printereditionProperty().get()))
                                                       .findAny()
                                                       .ifPresent(foundEdition->
                                                                  {
                                                                        if (printerConfigFile.getTypeCode().equalsIgnoreCase("RBX10"))
                                                                            newIdentity.printerFriendlyNameProperty().set("RoboxPro");
                                                                        else    
                                                                            newIdentity.printerFriendlyNameProperty().set("Robox");

                                                                        printerToUse.setPrinterConfiguration(printerConfigFile);
                                                                        printerToUse.setPrinterEdition(foundEdition);
                                                                        try
                                                                        {
                                                                            printerToUse.updatePrinterIdentity(newIdentity);
                                                                            resetResult = RoboxResetIDResult.RESET_SUCCESSFUL;
                                                                        }
                                                                        catch (PrinterException ex)
                                                                        {
                                                                            steno.warning("Couldn't reset Printer ID");
                                                                        }
                                                                  });
                    }
                }
            }
            if (resetResult != RoboxResetIDResult.RESET_NOT_DONE)
            {
                closeDialog();
            }
        }
    }

    @FXML
    private void cancel()
    {
        resetResult = RoboxResetIDResult.RESET_CANCELLED;        
        closeDialog();
    }
    
    private void closeDialog()
    {
        Stage dialogStage = (Stage)mainVBox.getScene().getWindow();
        dialogStage.close();
    }

    public RoboxResetIDResult getResetResult()
    {
        return resetResult;
    }

    public void setPrinterToUse(Printer printerToUse)
    {
        this.printerToUse = printerToUse;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        resetInstructionLabel.replaceText(Lookup.i18n("resetPIDD.resetInstructions"));
        resetInstructionLabel.setTextAlignment(TextAlignment.LEFT); // Ignores this in the FXML for some reason.
        printerIDLabel.disableProperty().bind(tempSetRadioButton.selectedProperty());
        printerResetCodeLabel.disableProperty().bind(tempSetRadioButton.selectedProperty());
        printerIDCodeField.disableProperty().bind(tempSetRadioButton.selectedProperty());
        printerTypeCodeField.disableProperty().bind(tempSetRadioButton.selectedProperty());
        printerEditionField.disableProperty().bind(tempSetRadioButton.selectedProperty());
        printerWeekField.disableProperty().bind(tempSetRadioButton.selectedProperty());
        printerYearField.disableProperty().bind(tempSetRadioButton.selectedProperty());
        printerPONumberField.disableProperty().bind(tempSetRadioButton.selectedProperty());
        printerSerialNumberField.disableProperty().bind(tempSetRadioButton.selectedProperty());
        printerChecksumField.disableProperty().bind(tempSetRadioButton.selectedProperty());
        printerElectronicsVersionField.disableProperty().bind(tempSetRadioButton.selectedProperty());

        printerIDCodeField.textProperty().addListener((observable, oldValue, newValue) -> 
                                                      {
                                                          PrinterIdentity  identity = decryptIDCode(newValue.trim());
                                                          if (identity != null)
                                                          {
                                                              printerTypeCodeField.setText(identity.printermodelProperty().get());
                                                              printerEditionField.setText(identity.printereditionProperty().get());
                                                              printerWeekField.setText(identity.printerweekOfManufactureProperty().get());
                                                              printerYearField.setText(identity.printeryearOfManufactureProperty().get());
                                                              printerPONumberField.setText(identity.printerpoNumberProperty().get());
                                                              printerSerialNumberField.setText(identity.printerserialNumberProperty().get());
                                                              printerChecksumField.setText(identity.printercheckByteProperty().get());
                                                              if (!identity.printerelectronicsVersionProperty().get().isEmpty())
                                                                  printerElectronicsVersionField.setText(identity.printerelectronicsVersionProperty().get());
                                                              else
                                                                  printerElectronicsVersionField.clear();
                                                              identityValid.set(true);
                                                          }
                                                          else
                                                          {
                                                              printerTypeCodeField.clear();
                                                              printerEditionField.clear();
                                                              printerWeekField.clear();
                                                              printerYearField.clear();
                                                              printerPONumberField.clear();
                                                              printerSerialNumberField.clear();
                                                              printerChecksumField.clear();
                                                              printerElectronicsVersionField.clear();
                                                              identityValid.set(true);
                                                          }
                                                      });
        
        printerTypeLabel.disableProperty().bind(permSetRadioButton.selectedProperty());
        printerTypeChoice.disableProperty().bind(permSetRadioButton.selectedProperty());
        printerEditionChoice.disableProperty().bind(permSetRadioButton.selectedProperty());        

        printerTypeChoice.getSelectionModel()
                         .selectedIndexProperty()
                         .addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
                                      {
                                          updateEditionChoice(printerTypeChoice.getItems().get(newValue.intValue()));
                                      });

        resetButton.disableProperty().bind(permSetRadioButton.selectedProperty().and(identityValid.not()));        
    }
    
    public void updateFieldsFromPrinterID(PrinterIDResponse printerID)
    {
        tempSetRadioButton.setSelected(true);
        printerTypeCodeField.clear();
        printerEditionField.clear();
        printerWeekField.clear();
        printerYearField.clear();
        printerPONumberField.clear();
        printerSerialNumberField.clear();
        printerChecksumField.clear();

        printerTypeChoice.setItems(FXCollections.observableList(PrinterContainer.getCompletePrinterList()));
        printerTypeChoice.setValue(printerTypeChoice.getItems().get(0));
        updateEditionChoice(printerTypeChoice.getItems().get(0));
    }
    
    private void updateEditionChoice(PrinterDefinitionFile printerConfigFile)
    {
        List<String> l = new ArrayList<>();
        printerConfigFile.getEditions()
                         .forEach((edition) -> 
                                  {
                                      l.add(edition.getTypeCode());
                                  });
        printerEditionChoice.setItems(FXCollections.observableList(printerConfigFile.getEditions()));
        printerEditionChoice.setValue(printerEditionChoice.getItems().get(0));
    }
    
    private static String encrypt(final String plainMessage,
                                 final String symKeyHex)
    {
        
        try
        {
            final byte[] symKeyData = Hex.decodeHex(symKeyHex.toCharArray());
            final byte[] encodedMessage = plainMessage.getBytes(Charset.forName("UTF-8"));
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final int blockSize = cipher.getBlockSize();

            // create the key
            final SecretKeySpec symKey = new SecretKeySpec(symKeyData, "AES");

            // generate random IV using block size (possibly create a method for
            // this)
            final byte[] ivData = new byte[blockSize];
            final SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
            rnd.nextBytes(ivData);
            final IvParameterSpec iv = new IvParameterSpec(ivData);

            cipher.init(Cipher.ENCRYPT_MODE, symKey, iv);

            final byte[] encryptedMessage = cipher.doFinal(encodedMessage);

            // concatenate IV and encrypted message
            final byte[] ivAndEncryptedMessage = new byte[ivData.length
                    + encryptedMessage.length];
            System.arraycopy(ivData, 0, ivAndEncryptedMessage, 0, blockSize);
            System.arraycopy(encryptedMessage, 0, ivAndEncryptedMessage,
                    blockSize, encryptedMessage.length);

            final String ivAndEncryptedMessageBase64 = Base64.getEncoder().encodeToString(ivAndEncryptedMessage);

            return ivAndEncryptedMessageBase64;
        }
        catch (DecoderException e)
        {
            throw new IllegalArgumentException("Cannot decode symKeyHex");
        }
        catch (InvalidKeyException e)
        {
            throw new IllegalArgumentException("key argument does not contain a valid AES key");
        }
        catch (GeneralSecurityException e)
        {
            throw new IllegalStateException("Unexpected exception during encryption", e);
        }
    }

    private static String decrypt(final String ivAndEncryptedMessageBase64,
                                 final String symKeyHex)
    {
        try 
        {
            final byte[] symKeyData = Hex.decodeHex(symKeyHex.toCharArray());
            final byte[] ivAndEncryptedMessage = Base64.getDecoder().decode(ivAndEncryptedMessageBase64);
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final int blockSize = cipher.getBlockSize();

            // create the key
            final SecretKeySpec symKey = new SecretKeySpec(symKeyData, "AES");

            // retrieve random IV from start of the received message
            final byte[] ivData = new byte[blockSize];
            System.arraycopy(ivAndEncryptedMessage, 0, ivData, 0, blockSize);
            final IvParameterSpec iv = new IvParameterSpec(ivData);

            // retrieve the encrypted message itself
            final byte[] encryptedMessage = new byte[ivAndEncryptedMessage.length
                    - blockSize];
            System.arraycopy(ivAndEncryptedMessage, blockSize,
                    encryptedMessage, 0, encryptedMessage.length);

            cipher.init(Cipher.DECRYPT_MODE, symKey, iv);

            final byte[] encodedMessage = cipher.doFinal(encryptedMessage);

            // concatenate IV and encrypted message
            final String message = new String(encodedMessage,
                                              Charset.forName("UTF-8"));

            return message;
        }
        catch (DecoderException e)
        {
            throw new IllegalArgumentException("Cannot decode symKeyHex");
        }
        catch (InvalidKeyException e)
        {
            throw new IllegalArgumentException("key argument does not contain a valid AES key");
        }
        catch (BadPaddingException e)
        {
            // you'd better know about padding oracle attacks
            return null;
        }
        catch (GeneralSecurityException e)
        {
            throw new IllegalStateException("Unexpected exception during decryption", e);
        }
    }

    private PrinterIdentity decryptIDCode(String encryptedIDCode)
    {
        PrinterIdentity identity = null;
        try
        {
            String decrypted = decrypt(encryptedIDCode, KEY_TO_THE_CRYPT);
            String[] components = decrypted.split("-");
            if (components.length == 7)
            {
                PrinterIdentity newIdentity = new PrinterIdentity();
                newIdentity.printerColourProperty().set(Color.BLUE);
                newIdentity.printermodelProperty().set(components[0].trim().toUpperCase());
                newIdentity.printereditionProperty().set(components[1].trim().toUpperCase());
                newIdentity.printerweekOfManufactureProperty().set(components[2].trim());
                newIdentity.printeryearOfManufactureProperty().set(components[3].trim());
                newIdentity.printerpoNumberProperty().set(components[4].trim());
                newIdentity.printerserialNumberProperty().set(components[5].trim());
                String checkByte = components[6].trim();
                String electronicsVersion = "1";
                if (checkByte.length() == 3 && (checkByte.charAt(1) == 'E' || checkByte.charAt(1) == 'e')) {
                    electronicsVersion = checkByte.substring(2, 3);
                    checkByte = checkByte.substring(0, 1);
                }
                newIdentity.printercheckByteProperty().set(checkByte);
                newIdentity.printerelectronicsVersionProperty().set(electronicsVersion);
                newIdentity.firmwareVersionProperty().set("r762"); // Force a reload of the firmware.
                if (newIdentity.isValid())
                {
                    identity = newIdentity;
                }
            }
        }
        catch (IllegalArgumentException |  IllegalStateException ex)
        {
        }
        return identity;
    }
}
