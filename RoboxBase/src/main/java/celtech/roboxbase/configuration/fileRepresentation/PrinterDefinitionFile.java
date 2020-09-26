package celtech.roboxbase.configuration.fileRepresentation;

import celtech.roboxbase.configuration.hardwarevariants.PrinterType;
import java.util.List;

/**
 *
 * @author Ian
 */
public class PrinterDefinitionFile
{

    private int version = 1;
    private String typeCode;
    private String friendlyName;
    private List<PrinterEdition> editions;
    private List<String> compatibleHeads;
    private int printVolumeWidth;
    private int printVolumeDepth;
    private int printVolumeHeight;

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public String getTypeCode()
    {
        return typeCode;
    }

    public PrinterType getPrinterType() {
        return PrinterType.getPrinterTypeForTypeCode(typeCode);
    }

    public void setTypeCode(String typeCode)
    {
        this.typeCode = typeCode;
    }

    public String getFriendlyName()
    {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName)
    {
        this.friendlyName = friendlyName;
    }

    public List<PrinterEdition> getEditions()
    {
        return editions;
    }

    public void setEditions(List<PrinterEdition> editions)
    {
        this.editions = editions;
    }

    public List<String> getCompatibleHeads()
    {
        return compatibleHeads;
    }

    public void setCompatibleHeads(List<String> compatibleHeads)
    {
        this.compatibleHeads = compatibleHeads;
    }

    public int getPrintVolumeWidth()
    {
        return printVolumeWidth;
    }

    public void setPrintVolumeWidth(int printVolumeWidth)
    {
        this.printVolumeWidth = printVolumeWidth;
    }

    public int getPrintVolumeDepth()
    {
        return printVolumeDepth;
    }

    public void setPrintVolumeDepth(int printVolumeDepth)
    {
        this.printVolumeDepth = printVolumeDepth;
    }

    public int getPrintVolumeHeight()
    {
        return printVolumeHeight;
    }

    public void setPrintVolumeHeight(int printVolumeHeight)
    {
        this.printVolumeHeight = printVolumeHeight;
    }
    
    @Override
    public String toString()
    {
        if (friendlyName != null && friendlyName.length() > 0)
            return friendlyName;
        else
            return super.toString();
    } 
}
