package celtech.coreUI.components.material;

import celtech.roboxbase.configuration.Filament;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Ian
 */
public class FilamentSwatch extends StackPane
{

    @FXML
    private Rectangle swatchPanel;

    private Tooltip ttip = null;

    private FilamentSelectionListener filamentSelectionListener;
    private Filament currentFilament = null;

    public FilamentSwatch()
    {
        initialise(null, null);
    }

    public FilamentSwatch(FilamentSelectionListener filamentSelectionListenerInput, Filament filament)
    {
        initialise(filamentSelectionListenerInput, filament);
    }

    private void initialise(FilamentSelectionListener filamentSelectionListenerInput, Filament filament)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/celtech/resources/fxml/components/material/filamentSwatch.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        fxmlLoader.setClassLoader(this.getClass().getClassLoader());

        try
        {
            fxmlLoader.load();
        } catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }

        this.setPrefHeight(20);
        this.setPrefWidth(20);

        this.setOnMouseClicked((event) ->
        {
            if (filamentSelectionListener != null)
            {
                filamentSelectionListener.filamentSelected(filament);
            }
        });

        updateFilamentSelectionListener(filamentSelectionListenerInput);
        updateFilament(filament);
    }

    public void updateFilamentSelectionListener(FilamentSelectionListener filamentSelectionListener)
    {
        this.filamentSelectionListener = filamentSelectionListener;
    }

    public void updateFilament(Filament filament)
    {
        if (ttip != null)
        {
            Tooltip.uninstall(this, ttip);
        }

        if (filament != null)
        {
            ttip = new Tooltip(filament.getFriendlyFilamentName());
            Tooltip.install(this, ttip);

            swatchPanel.setFill(filament.getDisplayColour());
        }
        
        currentFilament = filament;
    }
    
        public Filament getFilament()
    {
        return currentFilament;
    }
}
