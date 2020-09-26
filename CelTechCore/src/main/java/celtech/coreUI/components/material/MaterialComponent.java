/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.components.material;

import celtech.Lookup;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.MaterialType;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.coreUI.StandardColours;
import celtech.roboxbase.BaseLookup;
import static celtech.roboxbase.utils.ColourStringConverter.colourToString;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterListChangesListener;
import celtech.roboxbase.printerControl.model.Reel;
import java.io.IOException;
import java.net.URL;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 *
 * @author tony
 */
public class MaterialComponent extends VBox implements PrinterListChangesListener, FilamentSelectionListener
{

    private Printer printer;
    private int extruderNumber;
    private final FilamentContainer filamentContainer = FilamentContainer.getInstance();

    public enum ReelType
    {

        ROBOX, GEARS, SOLID_QUESTION, SOLID_CROSS;
    }

    public enum Mode
    {

        STATUS, LAYOUT, SETTINGS;
    }

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Text reelNumberMaterial;

    @FXML
    private SVGPath reelSVGRobox;

    @FXML
    private SVGPath reelSVGGears;

    @FXML
    private Group reelSVGQuestion;

    @FXML
    private Group reelSVGCross;

    @FXML
    private SVGPath svgLoaded;

    @FXML
    private Text materialColour1;

    @FXML
    private Text materialColour2;

    @FXML
    private Text materialRemaining;

    @FXML
    private TextFlow materialColourContainer;

    @FXML
    private FilamentMenuButton filamentMenuButton;

    private Filament filamentInUse = FilamentContainer.UNKNOWN_FILAMENT;

    public MaterialComponent()
    {
        // Should only be called from scene builder
    }

    public MaterialComponent(Printer printer, int extruderNumber)
    {
        super();
        URL fxml = getClass().getResource(
                "/celtech/resources/fxml/components/material/material.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try
        {
            fxmlLoader.load();
        } catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }

        this.printer = printer;
        this.extruderNumber = extruderNumber;

        BaseLookup.getPrinterListChangesNotifier().addListener(this);

        setUpFilamentLoadedListener();
        filamentMenuButton.initialiseButton(this, null, true);
        configureDisplay();
    }

    private boolean filamentLoaded()
    {
        return printer.extrudersProperty().get(extruderNumber).filamentLoadedProperty().get();
    }

    private void whenMaterialSelected(Filament filament)
    {
        if (Lookup.getSelectedPrinterProperty().get() != null)
        {
            Lookup.getSelectedPrinterProperty().get().overrideFilament(extruderNumber, filament);
        }
        configureDisplay();
    }

    private void setUpFilamentLoadedListener()
    {
        if (printer != null && printer.extrudersProperty().get(extruderNumber) != null)
        {
            printer.extrudersProperty().get(extruderNumber).filamentLoadedProperty().addListener(
                    (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
            {
                configureDisplay();
            });
        }
    }

    private void configureDisplay()
    {
        materialColourContainer.setVisible(true);
        if (printer.reelsProperty().containsKey(extruderNumber))
        {
            //Reel is attached
            filamentMenuButton.setVisible(false);
            setReelType(ReelType.ROBOX);
            Reel reel = printer.reelsProperty().get(extruderNumber);
            filamentInUse = filamentContainer.getFilamentByID(reel.filamentIDProperty().get());
            materialRemaining.setVisible(true);
        } else if (printer.extrudersProperty().get(extruderNumber).filamentLoadedProperty().get())
        {
            //Loaded but no reel attached
            filamentMenuButton.setVisible(true);
            materialRemaining.setVisible(false);
            filamentInUse = printer.effectiveFilamentsProperty().get(extruderNumber);
            filamentMenuButton.displayFilamentOnButton(filamentInUse);
        } else
        {
            //No reel and not loaded
            filamentMenuButton.setVisible(false);
            materialRemaining.setVisible(false);
            resetFilament();
        }

        if (filamentInUse == FilamentContainer.UNKNOWN_FILAMENT
                && !printer.extrudersProperty().get(extruderNumber).filamentLoadedProperty().get())
        {
            svgLoaded.setVisible(false);
            setReelType(ReelType.SOLID_CROSS);
            String filamentNotLoaded = Lookup.i18n("materialComponent.filamentNotLoaded");
            showDetails((1 + extruderNumber) + ":", "", filamentNotLoaded, Color.BLACK, false, false);
        } else
        {
            Float remainingFilament = 0f;
            Float diameter = 0f;
            if (filamentInUse == FilamentContainer.UNKNOWN_FILAMENT)
            {
                svgLoaded.setVisible(true);
                setReelType(ReelType.SOLID_QUESTION);
                String materialUnknown = Lookup.i18n("materialComponent.materialUnknown");
                showDetails((1 + extruderNumber) + ":", "", materialUnknown,
                        Color.BLACK, true, true);
            } else
            {
                if (printer.reelsProperty().containsKey(extruderNumber))
                {
                    Reel reel = printer.reelsProperty().get(extruderNumber);
                    remainingFilament = reel.remainingFilamentProperty().get();
                    diameter = reel.diameterProperty().get();
                    if (filamentInUse.isMutable())
                    {
                        setReelType(ReelType.GEARS);
                    } else
                    {
                        setReelType(ReelType.ROBOX);
                    }
                } else
                {
                    setReelType(ReelType.GEARS);
                }
                setMaterial(extruderNumber, filamentInUse.getMaterial(),
                        filamentInUse.getFriendlyFilamentName(),
                        filamentInUse.getDisplayColourProperty().get(),
                        remainingFilament,
                        diameter, filamentLoaded());
            }
        }
    }

    public void setReelType(ReelType reelType)
    {
        reelSVGRobox.setVisible(false);
        reelSVGGears.setVisible(false);
        reelSVGQuestion.setVisible(false);
        reelSVGCross.setVisible(false);
        switch (reelType)
        {
            case ROBOX:
                reelSVGRobox.setVisible(true);
                break;
            case GEARS:
                reelSVGGears.setVisible(true);
                break;
            case SOLID_QUESTION:
                reelSVGQuestion.setVisible(true);
                break;
            case SOLID_CROSS:
                reelSVGCross.setVisible(true);
                break;
        }
    }

    private void setMaterial(int reelNumber, MaterialType materialType, String materialColourString,
            Color colour, double remainingFilament, double filamentDiameter, boolean filamentLoaded)
    {

        String numberMaterial = "";
        double densityKGM3 = 1;

        if (materialType != null)
        {
            numberMaterial = String.valueOf(reelNumber + 1) + ":"
                    + materialType.getFriendlyName();
            densityKGM3 = materialType.getDensity() * 1000d;
        } else
        {
            numberMaterial = String.valueOf(reelNumber + 1) + ":";
        }

        double remainingLengthMeters = remainingFilament / 1000d;
        if (remainingLengthMeters < 0)
        {
            remainingLengthMeters = 0;
        }
        double crossSectionM2 = Math.PI * filamentDiameter * filamentDiameter / 4d * 1e-6;
        double remainingWeightG = remainingLengthMeters * crossSectionM2 * densityKGM3 * 1000d;
        String remaining = ((int) remainingLengthMeters) + "m / " + ((int) remainingWeightG)
                + "g " + Lookup.i18n("materialComponent.remaining");

        showDetails(numberMaterial, remaining, materialColourString, colour, filamentLoaded, true);
    }

    private void showDetails(String numberMaterial, String materialRemainingString,
            String materialColourString, Color colour, boolean filamentLoaded,
            boolean dualWeightTitle)
    {

        svgLoaded.setVisible(filamentLoaded);

        reelNumberMaterial.setText(numberMaterial);
        materialRemaining.setText(materialRemainingString);
        String colourString = colourToString(colour);
        materialColourContainer.setStyle("-fx-background-color: #" + colourString + ";");

        if (extruderNumber == 0)
        {
            reelNumberMaterial.setStyle("-fx-fill: robox_blue;");
            svgLoaded.setFill(StandardColours.ROBOX_BLUE);
        } else
        {
            reelNumberMaterial.setStyle("-fx-fill: highlight_colour_orange;");
            svgLoaded.setFill(StandardColours.HIGHLIGHT_ORANGE);
        }
        setReelColourString(colourString);

        int endOfManufacturerSection = materialColourString.indexOf(' ');
        if (dualWeightTitle && endOfManufacturerSection > 0 && endOfManufacturerSection < materialColourString.length() - 1)
        {
            materialColour1.setText(materialColourString.substring(0, endOfManufacturerSection));
            materialColour2.setText(materialColourString.substring(endOfManufacturerSection));
        } else
        {
            materialColour1.setText("");
            materialColour2.setText(materialColourString);
        }

        if (colour.getBrightness() < 0.5)
        {
            materialColour1.setStyle("-fx-fill:white;");
            materialColour2.setStyle("-fx-fill:white;");
        } else
        {
            materialColour1.setStyle("-fx-fill:black;");
            materialColour2.setStyle("-fx-fill:black;");
        }
    }

    private void setReelColourString(String colourString)
    {
        reelSVGRobox.setStyle("-fx-fill: #" + colourString + ";");
        reelSVGGears.setStyle("-fx-fill: #" + colourString + ";");
    }

    // PrinterListChangesNotifier
    @Override
    public void whenPrinterAdded(Printer printer)
    {
    }

    @Override
    public void whenPrinterRemoved(Printer printer)
    {
    }

    @Override
    public void whenHeadAdded(Printer printer)
    {
    }

    @Override
    public void whenHeadRemoved(Printer printer, Head head)
    {
    }

    @Override
    public void whenReelAdded(Printer printer, int reelIndex)
    {
        if (this.printer == printer)
        {
            configureDisplay();
        }
    }

    private void resetFilament()
    {
        filamentInUse = FilamentContainer.UNKNOWN_FILAMENT;
    }

    @Override
    public void whenReelRemoved(Printer printer, Reel reel, int reelIndex)
    {
        if (this.printer == printer)
        {
            resetFilament();
            configureDisplay();
        }
    }

    @Override
    public void whenReelChanged(Printer printer, Reel reel)
    {
        if (this.printer == printer)
        {
            configureDisplay();
        }
    }

    @Override
    public void whenExtruderAdded(Printer printer, int extruderIndex)
    {
        if (this.printer == printer)
        {
            setUpFilamentLoadedListener();
        }
    }

    @Override
    public void whenExtruderRemoved(Printer printer, int extruderIndex)
    {
    }

    @Override
    public void filamentSelected(Filament filament)
    {
        whenMaterialSelected(filament);
        filamentMenuButton.hide();
    }
}
