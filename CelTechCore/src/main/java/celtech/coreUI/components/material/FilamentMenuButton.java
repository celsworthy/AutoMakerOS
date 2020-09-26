package celtech.coreUI.components.material;

import celtech.Lookup;
import celtech.roboxbase.MaterialType;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 *
 * @author Ian
 */
public class FilamentMenuButton extends MenuButton implements FilamentSelectionListener, FilamentContainer.FilamentDatabaseChangesListener
{

    private SelectedFilamentDisplayNode filamentDisplayNode = new SelectedFilamentDisplayNode();
    private FilamentOnReelDisplay filamentOnReelDisplayNode = new FilamentOnReelDisplay();
    private FilamentSelectionListener filamentSelectionListener = null;
    private SpecialItemSelectionListener specialItemSelectionListener = null;
    private boolean dontDisplayDuplicateNamedFilaments = false;

    private Map<String, FilamentOnReelMenuItem> permanentMenuItems = new TreeMap<>();
    private Map<String, Filament> permanentMenuFilaments = new HashMap<>();

    private static final String roboxCategoryPrefix = "Robox";
    private static final String customCategoryPrefix = "Custom";

    protected static Comparator<Filament> byCategory = ((Filament o1, Filament o2) ->
    {
        int comparisonStatus = o1.getCategory().compareTo(o2.getCategory());
        if (comparisonStatus > 0
                && (o1.getCategory().startsWith(roboxCategoryPrefix)
                && !o2.getCategory().startsWith(roboxCategoryPrefix))
                || (!o1.getCategory().startsWith(customCategoryPrefix)
                && o2.getCategory().startsWith(customCategoryPrefix)))

        {
            comparisonStatus = -1;
        } else if (comparisonStatus < 0
                && (!o1.getCategory().startsWith(roboxCategoryPrefix)
                && o2.getCategory().startsWith(roboxCategoryPrefix))
                || (o1.getCategory().startsWith(customCategoryPrefix)
                && !o2.getCategory().startsWith(customCategoryPrefix)))
        {
            comparisonStatus = 1;
        }
        return comparisonStatus;
    });

    protected static Comparator<String> byBrandName = ((String o1, String o2) ->
    {
        int comparisonStatus = o1.compareTo(o2);
        if (comparisonStatus > 0
                && (o1.startsWith(roboxCategoryPrefix)
                && !o2.startsWith(roboxCategoryPrefix))
                || (!o1.startsWith(customCategoryPrefix)
                && o2.startsWith(customCategoryPrefix)))
        {
            comparisonStatus = -1;
        } else if (comparisonStatus < 0
                && (!o1.startsWith(roboxCategoryPrefix)
                && o2.startsWith(roboxCategoryPrefix))
                || (o1.startsWith(customCategoryPrefix)
                && !o2.startsWith(customCategoryPrefix)))
        {
            comparisonStatus = 1;
        }
        return comparisonStatus;
    });

    private Comparator<Entry<MaterialType, List<Filament>>> byMaterialName
            = (Entry<MaterialType, List<Filament>> o1, Entry<MaterialType, List<Filament>> o2) -> o1.getKey().getFriendlyName().compareTo(o2.getKey().getFriendlyName());

    public FilamentMenuButton()
    {
        setGraphic(filamentDisplayNode);
        getStyleClass().add("filament-menu-button");

        Lookup.getUserPreferences().advancedModeProperty().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
        {
            repopulateFilaments();
        });

        FilamentContainer.getInstance().addFilamentDatabaseChangesListener(this);
    }

    /**
     * Returns the first filament in the control
     *
     * @param filamentSelectionListener
     * @param specialItemSelectionListener
     * @param dontDisplayDuplicateNamedFilaments
     * @return
     */
    public Filament initialiseButton(FilamentSelectionListener filamentSelectionListener,
            SpecialItemSelectionListener specialItemSelectionListener,
            boolean dontDisplayDuplicateNamedFilaments)
    {
        this.filamentSelectionListener = filamentSelectionListener;
        this.specialItemSelectionListener = specialItemSelectionListener;
        this.dontDisplayDuplicateNamedFilaments = dontDisplayDuplicateNamedFilaments;
        repopulateFilaments();
        return displayFirstFilament();
    }

    private void addSeparator()
    {
        SeparatorMenuItem separator = new SeparatorMenuItem();
        getItems().add(separator);
    }

    private void repopulateFilaments()
    {
        Map<String, Map<String, Map<MaterialType, List<Filament>>>> filamentsByBrand = new TreeMap<>(byBrandName);
        List<String> allTheFilamentNamesIHaveEverLoaded = new ArrayList<>();

        FilamentContainer.getInstance().getCompleteFilamentList().forEach(filament ->
        {
            String uniqueFilamentRef = filament.getFriendlyFilamentName() + filament.getBrand() + filament.getCategory() + filament.getMaterial().getFriendlyName();
            if (!allTheFilamentNamesIHaveEverLoaded.contains(uniqueFilamentRef))
            {
                String brand = filament.getBrand();
                String category = filament.getCategory();
                MaterialType materialType = filament.getMaterial();

                if (!filamentsByBrand.containsKey(brand))
                {
                    Map<String, Map<MaterialType, List<Filament>>> filamentCategoryGroupList = new TreeMap<>();
                    filamentsByBrand.put(brand, filamentCategoryGroupList);
                }

                if (!filamentsByBrand.get(brand).containsKey(category))
                {
                    Map<MaterialType, List<Filament>> categoryMap = new TreeMap<>();
                    filamentsByBrand.get(brand).put(category, categoryMap);
                }

                if (!filamentsByBrand.get(brand).get(category).containsKey(materialType))
                {
                    List<Filament> filamentList = new ArrayList<>();
                    filamentsByBrand.get(brand).get(category).put(materialType, filamentList);
                }

                filamentsByBrand.get(brand).get(category).get(materialType).add(filament);

                allTheFilamentNamesIHaveEverLoaded.add(uniqueFilamentRef);
            }
        });

        getItems().clear();

        boolean firstItem = true;

        for (Map.Entry<String, FilamentOnReelMenuItem> permanentMenuItem
                : permanentMenuItems.entrySet())
        {
            if (!firstItem)
            {
                addSeparator();
            }
            getItems().add(permanentMenuItem.getValue());
            firstItem = false;
        }

        for (Map.Entry<String, Map<String, Map<MaterialType, List<Filament>>>> entry
                : filamentsByBrand.entrySet())
        {
            if (!firstItem)
            {
                addSeparator();
            }
            String brand = entry.getKey();
            Map<String, Map<MaterialType, List<Filament>>> filamentCategoryMap = entry.getValue();
            FilamentCategory filCat = new FilamentCategory(this);
            filCat.setCategoryData(brand, filamentCategoryMap);
            FilamentCategoryMenuItem filCatMenuItem = new FilamentCategoryMenuItem(filCat);
            if (brand.equalsIgnoreCase("custom"))
            {
                filCatMenuItem.getStyleClass().add("custom-filament-category");
            }

            filCatMenuItem.setHideOnClick(false);
            getItems().add(filCatMenuItem);
            firstItem = false;
        }
    }

    public Filament displayFirstFilament()
    {
        Filament firstFilament = null;

        for (MenuItem menuItem : getItems())
        {
            if (menuItem instanceof FilamentOnReelMenuItem)
            {
                Iterator<Entry<String, FilamentOnReelMenuItem>> permanentMenuItemIterator = permanentMenuItems.entrySet().iterator();
                while (permanentMenuItemIterator.hasNext())
                {
                    Entry<String, FilamentOnReelMenuItem> foundItem = permanentMenuItemIterator.next();
                    if (foundItem.getValue() == menuItem)
                    {
                        firstFilament = permanentMenuFilaments.get(foundItem.getKey());
                        displaySpecialItemOnButton(foundItem.getKey());
                        break;
                    }
                }
                break;
            } else if (menuItem instanceof FilamentCategoryMenuItem)
            {
                FilamentCategoryMenuItem filCatMenuItem = (FilamentCategoryMenuItem) menuItem;
                FilamentCategory filCat = (FilamentCategory) filCatMenuItem.getContent();
                Iterator<Entry<String, Map<MaterialType, List<Filament>>>> iterator = filCat.getFilamentMap().entrySet().iterator();
                if (iterator.hasNext())
                {
                    List<Filament> availableFilaments = iterator.next().getValue().values().iterator().next();
                    if (availableFilaments.size() > 0)
                    {
                        firstFilament = availableFilaments.get(0);
                        displayFilamentOnButton(firstFilament);
                    }
                }
                break;
            }
        }

        return firstFilament;
    }

    public void displayFilamentOnButton(Filament filamentToDisplay)
    {
        filamentDisplayNode.updateSelectedFilament(filamentToDisplay);
        setGraphic(filamentDisplayNode);
    }

    public void displaySpecialItemOnButton(String title)
    {
        filamentOnReelDisplayNode.updateFilamentOnReelDisplay(title, permanentMenuFilaments.get(title));
        setGraphic(filamentOnReelDisplayNode);
    }

    public Filament getCurrentlyDisplayedFilament()
    {
        if (getGraphic() == filamentDisplayNode)
        {
            return filamentDisplayNode.getSelectedFilament();
        } else
        {
            //Must be a special
            return filamentOnReelDisplayNode.getSelectedFilament();
        }
    }

    public void deleteSpecialMenuItem(String title)
    {
        permanentMenuItems.remove(title);
        permanentMenuFilaments.remove(title);
        repopulateFilaments();
    }

    public void addSpecialMenuItem(String title, Filament filament)
    {
        FilamentOnReelMenuItem newMenuItem = new FilamentOnReelMenuItem(title, filament, getPrefWidth());
        newMenuItem.setOnAction((event) ->
        {
            specialItemSelectedAction(title);
        });
        permanentMenuItems.put(title, newMenuItem);
        permanentMenuFilaments.put(title, filament);
        repopulateFilaments();
    }

    private void specialItemSelectedAction(String title)
    {
        displaySpecialItemOnButton(title);
        specialItemSelectionListener.specialItemSelected(title);
        hide();
    }

    private void filamentSelectedAction(Filament filament)
    {
        displayFilamentOnButton(filament);
        filamentSelectionListener.filamentSelected(filament);
        hide();
    }

    //Proxy the filament selection from the swatch
    @Override
    public void filamentSelected(Filament filament)
    {
        filamentSelectedAction(filament);
    }

    @Override
    public void whenFilamentChanges(String filamentId)
    {
        repopulateFilaments();
    }
}
