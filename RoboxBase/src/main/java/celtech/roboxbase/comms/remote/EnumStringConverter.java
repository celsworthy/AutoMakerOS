/*
 * Copyright 2014 CEL UK
 */

package celtech.roboxbase.comms.remote;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author tony
 */
public class EnumStringConverter
{
    
    private static final int CHAR_A_VAL = 65;
    
    /**
     * Convert the given int (which must be less than 256 in value) to a single-char string.
     */
    public static String intToString(int intVal)
    {
        assert (intVal < 256 - CHAR_A_VAL);
        byte[] bytes = new byte[1];
        // Add CHAR_A_VAL so 0 -> A, 1-> B etc. 
        byte val = (byte) (intVal + CHAR_A_VAL);
        bytes[0] = val;
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Convert a string (encoded by intToString) back to an int.
     */
    public static int stringToInt(String valString)
    {
        byte[] vals = valString.getBytes(StandardCharsets.UTF_8);
        return (int) (vals[0] - CHAR_A_VAL);
    }
    
}
