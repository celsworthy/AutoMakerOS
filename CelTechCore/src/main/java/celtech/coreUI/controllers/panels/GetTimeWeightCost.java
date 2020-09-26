/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.appManager.ModelContainerProject;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.services.gcodegenerator.GCodeGeneratorResult;
import celtech.roboxbase.services.slicer.PrintQualityEnumeration;
import celtech.roboxbase.utils.tasks.Cancellable;
import java.util.Optional;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * This class uses SlicerTask and PostProcessorTask to get the estimated time,
 * weight and cost for the given project and settings.
 *
 * @author tony
 */
public class GetTimeWeightCost
{
    public enum UpdateResult
    {
        NOT_DONE, 
        SUCCESS, 
        FAILED
    }
    
    private final Stenographer steno = StenographerFactory.getStenographer(
            GetTimeWeightCost.class.getName());

    //We are allowed to use ModelContainerProject here since this class can only run calcs for projects with meshes
    private final ModelContainerProject project;
    private final Label lblTime;
    private final Label lblWeight;
    private final Label lblCost;
    
    private final Cancellable cancellable;

    public GetTimeWeightCost(ModelContainerProject project,
            Label lblTime, Label lblWeight, Label lblCost, Cancellable cancellable)
    {
        this.project = project;
        this.lblTime = lblTime;
        this.lblWeight = lblWeight;
        this.lblCost = lblCost;
        this.cancellable = cancellable;

        cancellable.cancelled().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                {
                    showCancelled();
                });
    }

    private void showCancelled()
    {
        String cancelled = Lookup.i18n("timeCost.cancelled");
        BaseLookup.getTaskExecutor().runOnGUIThread(() ->
        {
            lblTime.setText(cancelled);
            lblWeight.setText(cancelled);
            lblCost.setText(cancelled);
        });
    }

    private boolean isCancelled()
    {
        return cancellable.cancelled().get();
    }

    public void updateFromProject(PrintQualityEnumeration printQuality)
    {
        ModelContainerProject mProject = (ModelContainerProject)project;
        Printer printer = Lookup.getSelectedPrinterProperty().get();
        Optional<GCodeGeneratorResult> resultOpt = mProject.getGCodeGenManager().getPrepResult(printQuality);
        if (resultOpt.isPresent())
        {
            if (resultOpt.get().isSuccess())
            {
                GCodeGeneratorResult result = resultOpt.get();
                if (result.getPrintJobStatistics().isPresent())
                {
                    BaseLookup.getTaskExecutor().runOnGUIThread(() ->
                    {
                        updateFieldsForStatistics(result.getPrintJobStatistics().get(), printer);
                    });
                }
            }
            else if (!isCancelled())
            {
                // Result failed. Note that the fields are already updated in response to a cancel.
                steno.error("Error with gCode preparation");
                String failed = Lookup.i18n("timeCost.failed");
                BaseLookup.getTaskExecutor().runOnGUIThread(() ->
                {
                    lblTime.setText(failed);
                    lblWeight.setText(failed);
                    lblCost.setText(failed);
                });
            }
        }
        else
        {
            // Result not computed
            BaseLookup.getTaskExecutor().runOnGUIThread(() ->
            {
                lblTime.setText("...");
                lblWeight.setText("...");
                lblCost.setText("...");
            });
        }
    }
    
    /**
     * Update the time/cost/weight fields based on the given statistics.
     */
    private void updateFieldsForStatistics(PrintJobStatistics printJobStatistics, Printer printer)
    {
        String formattedDuration = formatDuration(printJobStatistics.getPredictedDuration());

        double eVolumeUsed = printJobStatistics.geteVolumeUsed();
        double dVolumeUsed = printJobStatistics.getdVolumeUsed();

        Filament filament0 = FilamentContainer.UNKNOWN_FILAMENT;
        Filament filament1 = FilamentContainer.UNKNOWN_FILAMENT;

        if (printer != null)
        {
            filament0 = printer.effectiveFilamentsProperty().get(0);
            filament1 = printer.effectiveFilamentsProperty().get(1);
        }

        lblTime.setText(formattedDuration);

        double weight = 0;
        double costGBP = 0;

        if (filament0 == FilamentContainer.UNKNOWN_FILAMENT
                && filament1 == FilamentContainer.UNKNOWN_FILAMENT)
        {
            // If there is no filament loaded...
            String noFilament = Lookup.i18n("timeCost.noFilament");
            lblWeight.setText(noFilament);
            lblCost.setText(noFilament);
        } else
        {
            if (filament0 != FilamentContainer.UNKNOWN_FILAMENT)
            {
                weight += filament0.getWeightForVolume(eVolumeUsed * 1e-9);
                costGBP += filament0.getCostForVolume(eVolumeUsed * 1e-9);
            }

            if (filament1 != FilamentContainer.UNKNOWN_FILAMENT)
            {
                weight += filament1.getWeightForVolume(dVolumeUsed * 1e-9);
                costGBP += filament1.getCostForVolume(dVolumeUsed * 1e-9);
            }

            String formattedWeight = formatWeight(weight);
            String formattedCost = formatCost(costGBP);
            lblWeight.setText(formattedWeight);
            lblCost.setText(formattedCost);
        }
    }

    /**
     * Take the duration in seconds and return a string in the format H MM.
     */
    private String formatDuration(double duration)
    {
        int SECONDS_PER_HOUR = 3600;
        int numHours = (int) (duration / SECONDS_PER_HOUR);
        int numMinutes = (int) ((duration - (numHours * SECONDS_PER_HOUR)) / 60);
        return String.format("%sh %sm", numHours, numMinutes);
    }

    /**
     * Take the weight in grammes and return a string in the format NNNg.
     */
    private String formatWeight(double weight)
    {
        return String.format("%sg", (int) weight);
    }

    /**
     * Take the cost in pounds and return a string in the format £1.43.
     */
    private String formatCost(final double cost)
    {
        double convertedCost = cost * Lookup.getUserPreferences().getcurrencyGBPToLocalMultiplier();
        int numPounds = (int) convertedCost;
        int numPence = (int) ((convertedCost - numPounds) * 100);
        return String.format(Lookup.getUserPreferences().getCurrencySymbol().getDisplayString() + "%s.%02d", numPounds, numPence);
    }

}
