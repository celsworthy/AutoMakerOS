/*
 * Copyright 2015 CEL UK
 */
package celtech.roboxbase.printerControl.model.statetransitions.purge;

import celtech.roboxbase.BaseLookup;

/**
 *
 * @author tony
 */
public enum PurgeState
{

    IDLE("purgeMaterial.explanation", true),
    INITIALISING("purgeMaterial.temperatureInstruction", true),
    CONFIRM_TEMPERATURE("purgeMaterial.temperatureInstruction", true),
    HEATING("purgeMaterial.heating", true),
    RUNNING_PURGE("purgeMaterial.inProgress", true),
    FINISHED("purgeMaterial.purgeComplete", false),
    CANCELLED("purgeMaterial.cancelled", false),
    CANCELLING("purgeMaterial.cancelling", false),
    DONE("purgeMaterial.done", false),
    FAILED("purgeMaterial.failed", false);

    private final String stepTitleResource;
    private boolean showCancel;

    private PurgeState(String stepTitleResource, boolean showCancel)
    {
        this.stepTitleResource = stepTitleResource;
        this.showCancel = showCancel;
    }

    public String getStepTitle()
    {
        if (stepTitleResource != null)
        {
            return BaseLookup.i18n(stepTitleResource);
        } else
        {
            return "";
        }
    }

    public boolean showCancelButton()
    {
        return showCancel;
    }

}
