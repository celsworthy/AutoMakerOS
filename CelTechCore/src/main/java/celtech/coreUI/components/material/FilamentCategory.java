package celtech.coreUI.components.material;

import celtech.roboxbase.MaterialType;
import celtech.roboxbase.configuration.Filament;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 *
 * @author Ian
 */
public class FilamentCategory extends VBox
{

    @FXML
    private Text swatchPatchTitle;

    @FXML
    private VBox swatchContainer;

    private Comparator<Entry<MaterialType, List<Filament>>> byMaterialName
            = (Entry<MaterialType, List<Filament>> o1, Entry<MaterialType, List<Filament>> o2) -> o1.getKey().getFriendlyName().compareTo(o2.getKey().getFriendlyName());
    private Comparator<Filament> byColour
            = (Filament o1, Filament o2) -> o1.getDisplayColour().toString().compareTo(o2.getDisplayColour().toString());

    private final FilamentSelectionListener materialSelectionListener;
    private Map<String, Map<MaterialType, List<Filament>>> filamentCategoryMap = null;

    public FilamentCategory(FilamentSelectionListener materialSelectionListener)
    {
        this.materialSelectionListener = materialSelectionListener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/celtech/resources/fxml/components/material/filamentCategory.fxml"));
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

        this.getStyleClass().add("filament-category");
    }

    public void setCategoryData(String brand, Map<String, Map<MaterialType, List<Filament>>> filamentCategoryMap)
    {
        swatchPatchTitle.setText(brand);

        swatchContainer.getChildren().clear();

        this.filamentCategoryMap = filamentCategoryMap;

        filamentCategoryMap.entrySet().stream().forEach((categoryEntry) ->
        {
            String category = categoryEntry.getKey();
            Map<MaterialType, List<Filament>> materialMap = categoryEntry.getValue();

            boolean needToAddCategory = false;

            if (materialMap.keySet().size() == 1)
            {
                needToAddCategory = true;
            } else
            {
                Text categoryTitle = new Text(category);
                categoryTitle.getStyleClass().add("filament-display-category");
                swatchContainer.getChildren().add(categoryTitle);
            }
            
            final boolean addCategoryDirective = needToAddCategory;

            materialMap.entrySet().stream().sorted(byMaterialName).forEach((materialEntry) ->
            {
                MaterialType material = materialEntry.getKey();
                List<Filament> filaments = materialEntry.getValue();

                Text materialTitle = new Text();
                materialTitle.getStyleClass().add("filament-display-material");
                
                if (addCategoryDirective)
                {
                    materialTitle.setText(category + " " + material.getFriendlyName());
                }
                else
                {
                    materialTitle.setText(material.getFriendlyName());
                }
                
                swatchContainer.getChildren().add(materialTitle);

                FlowPane flowPane = new FlowPane();
                swatchContainer.getChildren().add(flowPane);

                for (Filament filament : filaments)
                {
                    FilamentSwatch swatch = new FilamentSwatch(materialSelectionListener, filament);
                    flowPane.getChildren().add(swatch);
                }
            });
        });
    }

    public Map<String, Map<MaterialType, List<Filament>>> getFilamentMap()
    {
        return filamentCategoryMap;
    }

}
