/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase;

import celtech.roboxbase.utils.BaseEnvironmentConfiguredTest;
import static celtech.roboxbase.utils.ColourStringConverter.colourToString;
import static celtech.roboxbase.utils.ColourStringConverter.stringToColor;
import javafx.scene.paint.Color;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tony
 */
public class ColourStringConverterTest extends BaseEnvironmentConfiguredTest
{

    @Test
    public void testColourToString()
    {
        Color colour = Color.rgb(0x10, 0x20, 0x30);
        String strColour = colourToString(colour);
        assertEquals("102030", strColour);
    }

    @Test
    public void testStringToColor()
    {
        String colourStr = "102030";
        assertEquals(Color.rgb(0x10, 0x20, 0x30), stringToColor(colourStr));
    }

    @Test
    public void testColourToStringAndBack()
    {
        Color colour = Color.valueOf("0x1ed32fff");
        System.out.println("color is " + colour.toString());
        String strColour = colourToString(colour);
        assertEquals("1ED32F", strColour);
        Color readColor = stringToColor(strColour);
        assertEquals(Color.valueOf("0x1ed32fff"), readColor);
    }

}
