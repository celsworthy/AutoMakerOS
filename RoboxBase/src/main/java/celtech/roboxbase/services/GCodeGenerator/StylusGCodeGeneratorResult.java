/*
 * 
 */
package celtech.roboxbase.services.gcodegenerator;

import celtech.roboxbase.postprocessor.PrintJobStatistics;
import java.util.Optional;

/**
 *
 * @author Ian
 */
public class StylusGCodeGeneratorResult
{
    private boolean cancelled = false;
    private String rawOutputFileName = null;
    private String compensatedOutputFileName = null;
    private boolean hasDragKnife = false;
    private boolean resultOK = false;
    
    public StylusGCodeGeneratorResult()
    {
    }

    /**
     *
     * @return boolean
     */
    public boolean isSuccess()
    {
        return (cancelled != true &&
                resultOK != false);
    }

    /**
     *
     * @param cancelled
     */
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    /**
     *
     * @return boolean
     */
    public boolean isCancelled()
    {
        return cancelled;
    }

    /**
     *
     * @param hasDragKnife
     */
    public void setHasDragKnife(boolean hasDragKnife)
    {
        this.hasDragKnife = hasDragKnife;
    }

    /**
     *
     * @return boolean
     */
    public boolean getHasDragKnife()
    {
        return hasDragKnife;
    }

    /**
     *
     * @param resultOK
     */
    public void setResultOK(boolean resultOK)
    {
        this.resultOK = resultOK;
    }

    /**
     *
     * @return boolean
     */
    public boolean getResultOK()
    {
        return resultOK;
    }

    /**
     *
     * @return String
     */
    public String getRawOutputFileName()
    {
        return rawOutputFileName;
    }

    /**
     *
     * @param String
     */
    public void setRawOutputFileName(String rawOutputFileName)
    {
        this.rawOutputFileName = rawOutputFileName;
    }

    /**
     *
     * @return String
     */
    public String getCompensatedOutputFileName()
    {
        return compensatedOutputFileName;
    }

    /**
     *
     * @param String
     */
    public void setCompensatedOutputFileName(String compensatedOutputFileName)
    {
        this.compensatedOutputFileName = compensatedOutputFileName;
    }

    /**
     *
     * @return PrintJobStatistics
     */
    public Optional<PrintJobStatistics> getPrintJobStatistics()
    {
        return Optional.empty();
    }
}

