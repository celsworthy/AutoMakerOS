/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.components;

import celtech.coreUI.StandardColours;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 *
 * @author tony
 */
public class VerticalMenu extends VBox
{

    public interface NoArgsVoidFunc
    {

        void run() throws Exception;
    }

    private static final int SQUARE_SIZE = 16;
    private static final int ROW_HEIGHT = 50;

    private Item selectedItem;

    @FXML
    private GridPane verticalMenuGrid;

    @FXML
    private Text verticalMenuTitle;

    /**
     * The row number of the next text to be added.
     */
    private int nextRowNum = 2;
    private boolean disableNonSelectedItems;

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private Set<Item> allItems = new HashSet<>();
    private Map<Item, NoArgsVoidFunc> itemCallbacks = new HashMap<>();

    private boolean firstItemInitialised = false;
    private Item firstItem;

    class Item
    {

        String name;
        Text text;
        Rectangle square;
        Boolean predicateEnabled;

        public Item(String itemName)
        {
            name = itemName;
            text = new Text(itemName);
            text.getStyleClass().add("verticalMenuOption");
            square = new Rectangle();
            square.getStyleClass().add("verticalMenuSquare");
            square.setHeight(SQUARE_SIZE);
            square.setWidth(SQUARE_SIZE);
        }

        ChangeListener<Boolean> enabledListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
        {
            whenPredicateEnabledChanged(newValue);
        };

        private void whenPredicateEnabledChanged(Boolean newValue)
        {
            predicateEnabled = newValue;
            setEnabled(newValue);
        }

        private void setEnabledPredicate(ReadOnlyBooleanProperty enabledPredicate)
        {
            enabledPredicate.addListener(enabledListener);
            whenPredicateEnabledChanged(enabledPredicate.get());
        }

        private void setEnabled(Boolean enabled)
        {
            if (enabled && predicateEnabled != null && !predicateEnabled)
            {
                // don't enable if predicate says no
                return;
            }
            text.disableProperty().set(!enabled);
        }

        private void deselect()
        {
            square.setVisible(false);
            text.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false);
            square.setFill(Color.WHITE);
        }

        private void displayAsSelected()
        {
            square.setVisible(true);
            text.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true);
            square.setFill(StandardColours.ROBOX_BLUE);
        }
    }

    public VerticalMenu()
    {
        super();
        URL fxml = getClass().getResource("/celtech/resources/fxml/components/verticalMenu.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setClassLoader(getClass().getClassLoader());

        try
        {
            fxmlLoader.load();
        } catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }

    }

    public void setTitle(String title)
    {
        verticalMenuTitle.setText(title);
    }

    /**
     * Add a menu item to the menu. If enabledPredicate is not null then it governs if the item is
     * enabled or not.
     */
    public void addItem(String itemName, NoArgsVoidFunc callback,
        ReadOnlyBooleanProperty enabledPredicate)
    {
        Item item = new Item(itemName);
        addRow(verticalMenuGrid, item);
        allItems.add(item);
        itemCallbacks.put(item, callback);
        setUpEventHandlersForItem(item);

        if (!firstItemInitialised)
        {
            firstItem = item;
            firstItemInitialised = true;
        }

        if (enabledPredicate != null)
        {
            item.setEnabledPredicate(enabledPredicate);
        }
    }

    /**
     * Select the first (top) item.
     */
    public void selectFirstItem()
    {
        selectItem(firstItem);
    }

    /**
     * Programmatically select the item of the given name. If no item has that name then do nothing.
     */
    public void selectItemOfName(String itemName)
    {

        for (Item item : allItems)
        {
            if (item.name.equals(itemName))
            {
                deselectSelectedItem();
                selectItem(item);
                return;
            }
        }
    }

    private void setUpEventHandlersForItem(Item item)
    {
        item.square.setVisible(false);
        item.text.setOnMouseEntered((MouseEvent e) ->
        {
            if (item != selectedItem && !disableNonSelectedItems)
            {
                item.square.setVisible(true);
                item.square.setFill(Color.WHITE);
            }
        });
        item.text.setOnMouseExited((MouseEvent e) ->
        {
            if (item != selectedItem && !disableNonSelectedItems)
            {
                item.square.setVisible(false);
            }

        });
        item.text.setOnMouseClicked((MouseEvent e) ->
        {
            if (item != selectedItem && !disableNonSelectedItems)
            {
                if (selectedItem != null)
                {
                    selectedItem.deselect();
                }

                selectItem(item);
            }
        });
    }

    private void selectItem(Item item)
    {
        NoArgsVoidFunc callback = itemCallbacks.get(item);
        selectedItem = item;
        selectedItem.displayAsSelected();
        try
        {
            callback.run();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Add the given controls to a new row in the grid pane.
     */
    private void addRow(GridPane menuGrid, Item item)
    {
        menuGrid.add(item.square, 0, nextRowNum);
        menuGrid.add(item.text, 1, nextRowNum);
        menuGrid.getRowConstraints().add(nextRowNum, new RowConstraints(ROW_HEIGHT, ROW_HEIGHT,
                                                                        ROW_HEIGHT));
        nextRowNum++;
    }

    /**
     * ***
     * Disable all menu items except the currently selected item.
     */
    public void disableNonSelectedItems()
    {
        disableNonSelectedItems = true;
        for (Item item : allItems)
        {
            if (item != selectedItem)
            {
                item.setEnabled(false);
            }
        }
    }

    /**
     * *
     * Enable all menu items.
     */
    public void enableNonSelectedItems()
    {
        disableNonSelectedItems = false;
        for (Item item : allItems)
        {
            if (item != selectedItem)
            {
                item.setEnabled(true);
            }
        }
    }

    /**
     * If an item is selected then deselect it.
     */
    public void deselectSelectedItem()
    {
        if (selectedItem != null)
        {
            selectedItem.deselect();
            selectedItem = null;
        }
    }

    /**
     * Reset the menu.
     */
    public void reset()
    {
        enableNonSelectedItems();
        deselectSelectedItem();
    }
}
