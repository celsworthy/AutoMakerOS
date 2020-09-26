package celtech.roboxbase.services.postProcessor;

import celtech.roboxbase.postprocessor.RoboxiserResult;
import celtech.roboxbase.printerControl.model.Printer;

/**
 *
 * @author ianhudson
 */
public class GCodePostProcessingResult
{
    private String printJobUUID = null;
    private String outputFilename = null;
    private Printer printerToUse = null;
    private RoboxiserResult result = null;

    /**
     *
     * @param printJobUUID
     * @param outputFilename
     * @param printerToUse
     * @param result
     */
    public GCodePostProcessingResult(String printJobUUID, String outputFilename, Printer printerToUse, RoboxiserResult result)
    {
        this.printJobUUID = printJobUUID;
        this.outputFilename = outputFilename;
        this.printerToUse = printerToUse;
        this.result = result;
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
     * @return
     */
    public String getOutputFilename()
    {
        return outputFilename;
    }

    /**
     *
     * @return
     */
    public Printer getPrinterToUse()
    {
        return printerToUse;
    }

    /**
     *
     * @return
     */
    public RoboxiserResult getRoboxiserResult()
    {
        return result;
    }

    /**
     *
     * @param result
     */
    public void setRoboxiserResult(RoboxiserResult result)
    {
        this.result = result;
    }
}
