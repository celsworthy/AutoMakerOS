/*
 * 
 */
package celtech.roboxbase.services.gcodegenerator;

import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.services.postProcessor.GCodePostProcessingResult;
import celtech.roboxbase.services.slicer.SliceResult;
import java.util.Optional;

/**
 *
 * @author Ian
 */
public class GCodeGeneratorResult
{
    private boolean cancelled = false;
    private String slicerOutputFileName = null;
    private String postProcOutputFileName = null;
    private SliceResult slicerResult = null;
    private GCodePostProcessingResult postProcessingResult = null;
    
    public GCodeGeneratorResult()
    {
    }

    /**
     *
     * @return boolean
     */
    public boolean isSuccess()
    {
        return (cancelled != true &&
                slicerResult != null &&
                slicerResult.isSuccess() &&
                postProcessingResult != null &&
                postProcessingResult.getRoboxiserResult().isSuccess());
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
     * @param slicerResult
     */
    public void setSlicerResult(SliceResult slicerResult, String outputFileName)
    {
        this.slicerResult = slicerResult;
        this.slicerOutputFileName = outputFileName;
    }

    /**
     *
     * @return SliceResult
     */
    public Optional<SliceResult> getSlicerResult()
    {
        return Optional.ofNullable(this.slicerResult);
    }

    /**
     *
     * @return String
     */
    public String getSlicerOutputFileName()
    {
        return slicerOutputFileName;
    }

    /**
     *
     * @param postProcessingResult
     */
    public void setPostProcessingResult(GCodePostProcessingResult postProcessingResult, String outputFileName)
    {
        this.postProcessingResult = postProcessingResult;
        this.postProcOutputFileName = outputFileName;
    }

    /**
     *
     * @return GCodePostProcessingResult
     */
    public Optional<GCodePostProcessingResult> getPostProcessingResult()
    {
        return Optional.ofNullable(this.postProcessingResult);
    }

    /**
     *
     * @return String
     */
    public String getPostProcOutputFileName()
    {
        return postProcOutputFileName;
    }

    /**
     *
     * @return GCodePostProcessingResult
     */
    public Optional<PrintJobStatistics> getPrintJobStatistics()
    {
        return Optional.ofNullable(postProcessingResult != null
                                       ? postProcessingResult.getRoboxiserResult().getPrintJobStatistics()
                                       : null);
    }
}

