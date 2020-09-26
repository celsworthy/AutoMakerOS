/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.services.slicer;

/**
 *
 * @author ianhudson
 */
public class SlicedPrintJobIdentifier
{
    private int linesInPrintFile = 0;
    private String printJobUUID = null;

    /**
     *
     * @param linesInPrintFile
     * @param printJobUUID
     */
    public SlicedPrintJobIdentifier(int linesInPrintFile, String printJobUUID)
    {
        this.linesInPrintFile = linesInPrintFile;
        this.printJobUUID = printJobUUID;
    }

    /**
     *
     * @return
     */
    public int getLinesInPrintFile()
    {
        return linesInPrintFile;
    }

    /**
     *
     * @param linesInPrintFile
     */
    public void setLinesInPrintFile(int linesInPrintFile)
    {
        this.linesInPrintFile = linesInPrintFile;
    }

    /**
     *
     * @return
     */
    public String getPrintJobUUID()
    {
        return printJobUUID;
    }

    /**
     *
     * @param printJobUUID
     */
    public void setPrintJobUUID(String printJobUUID)
    {
        this.printJobUUID = printJobUUID;
    }
}
