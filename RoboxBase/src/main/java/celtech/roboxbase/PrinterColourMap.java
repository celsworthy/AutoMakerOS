/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javafx.scene.paint.Color;

/**
 *
 * @author Ian
 */
public class PrinterColourMap
{

    private static PrinterColourMap instance = null;
    private static HashMap<Color, Color> colourMap = new HashMap<>();
    private static ArrayList<Color> printerColours = new ArrayList<>();
    private static ArrayList<Color> displayColours = new ArrayList<>();

    private PrinterColourMap()
    {
        colourMap.put(Color.web("#7F6E32"), Color.web("#7F7F7F"));
        colourMap.put(Color.web("#FFD764"), Color.web("#FFFFFF"));
        colourMap.put(Color.web("#000000"), Color.web("#000000"));
        colourMap.put(Color.web("#000064"), Color.web("#00007F"));
        colourMap.put(Color.web("#006400"), Color.web("#007F00"));
        colourMap.put(Color.web("#640000"), Color.web("#7F0000"));
        colourMap.put(Color.web("#643214"), Color.web("#FF7F7F"));
        colourMap.put(Color.web("#34FF10"), Color.web("#7FFF7F"));
        colourMap.put(Color.web("#7F6EFF"), Color.web("#7F7FFF"));
        colourMap.put(Color.web("#7FFF00"), Color.web("#7FFF00"));
        colourMap.put(Color.web("#00FF00"), Color.web("#00FF00"));
        colourMap.put(Color.web("#00FF28"), Color.web("#00FF96"));
        colourMap.put(Color.web("#00FF78"), Color.web("#00FFFF"));
        colourMap.put(Color.web("#0000FF"), Color.web("#0000FF"));
        colourMap.put(Color.web("#BE00FF"), Color.web("#7F00FF"));
        colourMap.put(Color.web("#FF0082"), Color.web("#FF00FF"));
        colourMap.put(Color.web("#FF0000"), Color.web("#FF0000"));
        colourMap.put(Color.web("#FF4600"), Color.web("#FF7F00"));
        colourMap.put(Color.web("#FFC800"), Color.web("#FFFF00"));
        colourMap.put(Color.web("#FFFFFF"), Color.web("#A3A3A3"));
        
        printerColours = new ArrayList<>(colourMap.keySet());
        displayColours = new ArrayList<>(colourMap.values());
    }

    /**
     *
     * @return
     */
    public static PrinterColourMap getInstance()
    {
        if (instance == null)
        {
            instance = new PrinterColourMap();
        }

        return instance;
    }

    /**
     *
     * @param colour
     * @return
     */
    public Color printerToDisplayColour(Color colour)
    {
        return colourMap.get(colour);
    }

    /**
     *
     * @param colour
     * @return
     */
    public Color displayToPrinterColour(Color colour)
    {
        Color printerColour = null;

        for (Entry<Color, Color> colourEntry : colourMap.entrySet())
        {
            if (colourEntry.getValue().equals(colour))
            {
                printerColour = colourEntry.getKey();
                break;
            }
        }
        return printerColour;
    }

    public ArrayList<Color> getPrinterColours()
    {
        return printerColours;
    }

    public ArrayList<Color> getDisplayColours()
    {
        return displayColours;
    }
}
