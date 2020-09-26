/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.configuration.hardwarevariants;

/**
 * Valid printer models / types are declared here
 *
 * @author alynch
 */
public enum PrinterType 
{
    ROBOX("RBX01", "Robox\u00AE"),
    ROBOX_DUAL("RBX02", "RoboxDual\u2122"),
    ROBOX_PRO("RBX10", "RoboxPRO\u2122");

    private final String typeCode;
    private final String displayName;

    PrinterType(String typeCode, String displayName) 
    {
        this.typeCode = typeCode;
        this.displayName = displayName;
    }

    public String getTypeCode()
    {
        return typeCode;
    }
    
    public String getDisplayName()
    {
        return displayName;
    }

    public static PrinterType getPrinterTypeForTypeCode(String typeCode) 
    {
        for (PrinterType printerType : PrinterType.values())
        {
            if (printerType.getTypeCode().equalsIgnoreCase(typeCode))
            {
                return printerType;
            }
        }

        throw new RuntimeException("No printer type found for given code: " + typeCode);
    }
    
    public static PrinterType getPrinterTypeForDisplayName(String displayName)
    {
        for (PrinterType printerType : PrinterType.values())
        {
            if (printerType.getDisplayName().equalsIgnoreCase(displayName))
            {
                return printerType;
            }
        }

        throw new RuntimeException("No printer type found for given name: " + displayName);
    }
}
