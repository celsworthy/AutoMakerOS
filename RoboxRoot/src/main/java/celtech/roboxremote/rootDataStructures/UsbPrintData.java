package celtech.roboxremote.rootDataStructures;

/**
 *
 * @author George Salter
 */
public class UsbPrintData 
{
    
    private String printJobID;
    private String printJobPath;
    
    public UsbPrintData() 
    {
        // Jackson deserialization
    }

    public String getPrintJobID()
    {
        return printJobID;
    }

    public void setPrintJobID(String printJobID) 
    {
        this.printJobID = printJobID;
    }

    public String getPrintJobPath()
    {
        return printJobPath;
    }

    public void setPrintJobPath(String printJobPath) 
    {
        this.printJobPath = printJobPath;
    }
    
    @Override
    public String toString()
    {
        String usbPrintDataString = "USBPrintData:\n"
                + "Print Job ID: " + printJobID + "\n"
                + "Print Job path: " + printJobPath;
        return usbPrintDataString;
    }
}
