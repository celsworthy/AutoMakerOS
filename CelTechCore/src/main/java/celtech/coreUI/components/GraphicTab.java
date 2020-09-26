package celtech.coreUI.components;

import celtech.configuration.ApplicationConfiguration;
import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Tab;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author George Salter
 */
public class GraphicTab extends Tab 
{
    private static final Stenographer STENO = StenographerFactory.getStenographer(GraphicTab.class.getName());
    
    private final StringProperty fxmlIconName = new SimpleStringProperty("");
    private final StringProperty fxmlSelectedIconName = new SimpleStringProperty("");
    
    private final ChangeListener<Boolean> selectedTabChangeListener = (observable, oldValue, selected) -> {
        if (selected)
        {
            loadFXMLIcon(fxmlSelectedIconName.get());
        } else
        {
            loadFXMLIcon(fxmlIconName.get());
        }
    };
    
    public GraphicTab()
    {
        selectedProperty().addListener(selectedTabChangeListener);
    }
    
    public GraphicTab(String fxmlIconName)
    {
        setFxmlIconName(fxmlIconName);
        selectedProperty().addListener(selectedTabChangeListener);
    }
    
    public String getFxmlIconName()
    {
        return this.fxmlIconName.get();
    }
    
    public void setFxmlIconName(String fxmlFileName)
    {
        this.fxmlIconName.set(fxmlFileName);
        loadFXMLIcon(this.fxmlIconName.get());
    }
    
    public String getFxmlSelectedIconName()
    {
        return this.fxmlSelectedIconName.get();
    }
    
    public void setFxmlSelectedIconName(String fxmlSelectedFileName)
    {
        this.fxmlSelectedIconName.set(fxmlSelectedFileName);
    }
    
    private void loadFXMLIcon(String fxmlIconName)
    {
        if (fxmlIconName.equalsIgnoreCase("") == false)
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                ApplicationConfiguration.fxmlTabsResourcePath + fxmlIconName + ".fxml"));

            fxmlLoader.setClassLoader(this.getClass().getClassLoader());

            try
            {
                Group graphicGroup = fxmlLoader.load();
                setGraphic(graphicGroup);
            } catch (IOException ex)
            {
                STENO.exception("Could not load FXML from file: " + fxmlIconName + ".fxml", ex);
            }
        }
    }
}
