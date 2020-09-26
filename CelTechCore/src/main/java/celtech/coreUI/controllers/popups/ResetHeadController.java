package celtech.coreUI.controllers.popups;

import celtech.Lookup;
import celtech.configuration.ApplicationConfiguration;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.remote.EEPROMState;
import celtech.roboxbase.comms.rx.AckResponse;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class ResetHeadController implements Initializable
{

    private final Stenographer steno = StenographerFactory.getStenographer(ResetHeadController.class.getName());

    @FXML
    private FlowPane headHolder;

    @FXML
    private ScrollPane scroller;

    @FXML
    private void cancel()
    {
        BaseLookup.getSystemNotificationHandler().hideProgramInvalidHeadDialog();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        List<HeadFile> headFiles = new ArrayList(HeadContainer.getCompleteHeadList());

        headFiles.sort((HeadFile o1, HeadFile o2) -> o2.getTypeCode().compareTo(o1.getTypeCode()));

        for (HeadFile headFile : headFiles)
        {
            URL headImageURL = getClass().getResource(ApplicationConfiguration.imageResourcePath + "heads/" + headFile.getTypeCode() + "_FRONT.png");
            if (headImageURL == null)
            {
                headImageURL = getClass().getResource(ApplicationConfiguration.imageResourcePath + "heads/" + headFile.getTypeCode() + "-side.png");
            }
            if (headImageURL == null)
            {
                headImageURL = getClass().getResource(ApplicationConfiguration.imageResourcePath + "heads/Unknown.png");
            }

            ImageView image = new ImageView(headImageURL.toExternalForm());
            image.setFitHeight(300);
            image.setFitWidth(300);
            String headNamePrefix = "headPanel." + headFile.getTypeCode();
            String headNameBold = headNamePrefix + ".titleBold";
            String headNameLight = headNamePrefix + ".titleLight";
            String buttonText = "Unknown";
            if (Lookup.i18n(headNameBold) != null && Lookup.i18n(headNameLight) != null)
            {
                buttonText = Lookup.i18n(headNameBold) + Lookup.i18n(headNameLight);
            }
            Button imageButton = new Button(buttonText, image);
            imageButton.setPrefWidth(350);
            imageButton.setPrefHeight(350);
            imageButton.setContentDisplay(ContentDisplay.TOP);
            imageButton.setOnAction((ActionEvent t) ->
            {
                Printer currentPrinter = Lookup.getSelectedPrinterProperty().get();
                Head head = new Head(headFile);

                //Retain the last filament temperature and hours if they are available
                if (currentPrinter.getHeadEEPROMStateProperty().get() == EEPROMState.PROGRAMMED)
                {
                    if (currentPrinter.headProperty().get() != null)
                    {
                        head.headHoursProperty().set(currentPrinter.headProperty().get().headHoursProperty().get());

                        for (int nozzleHeaterCounter = 0; nozzleHeaterCounter < currentPrinter.headProperty().get().getNozzleHeaters().size(); nozzleHeaterCounter++)
                        {
                            if (head.getNozzleHeaters().size() > nozzleHeaterCounter)
                            {
                                head.getNozzleHeaters().get(nozzleHeaterCounter)
                                        .lastFilamentTemperatureProperty().set(currentPrinter.headProperty().get()
                                                .getNozzleHeaters().get(nozzleHeaterCounter).lastFilamentTemperatureProperty().get());
                            }
                        }

                        if (currentPrinter.headProperty().get().typeCodeProperty().get().equals(head.typeCodeProperty().get())
                                && currentPrinter.headProperty().get().getChecksum() != null
                                && !currentPrinter.headProperty().get().getChecksum().equals(""))
                        {
                            head.setUniqueID(currentPrinter.headProperty().get().typeCodeProperty().get(),
                                    currentPrinter.headProperty().get().getWeekNumber(),
                                    currentPrinter.headProperty().get().getYearNumber(),
                                    currentPrinter.headProperty().get().getPONumber(),
                                    currentPrinter.headProperty().get().getSerialNumber(),
                                    currentPrinter.headProperty().get().getChecksum());
                        }
                    }
                }

                try
                {
                    AckResponse formatResponse = currentPrinter.formatHeadEEPROM();
                    if (!formatResponse.isError())
                    {
                        currentPrinter.writeHeadEEPROM(head);
                    }
                } catch (PrinterException | RoboxCommsException ex)
                {
                    steno.exception("Couldn't format and write head data", ex);
                }

                BaseLookup.getSystemNotificationHandler().hideProgramInvalidHeadDialog();
            });
            headHolder.getChildren().add(imageButton);
        }
    }

}
