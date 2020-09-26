/*
 * Copyright 2014 CEL UK
 */
package celtech.services.modelLoader;

import celtech.appManager.Project;
import celtech.coreUI.visualisation.metaparts.ModelLoadResult;
import celtech.coreUI.visualisation.metaparts.ModelLoadResultType;
import java.util.List;

/**
 *
 * @author tony
 */
public class ModelLoadResults
{
    private final ModelLoadResultType type;
    private List<ModelLoadResult> results;
    private boolean shouldCentre = true;
    private Project referencedProject;

    public ModelLoadResults(ModelLoadResultType type, List<ModelLoadResult> results)
    {
        this.type = type;
        this.results = results;
    }

    public ModelLoadResultType getType()
    {
        return type;
    }

    public void setResults(List<ModelLoadResult> results)
    {
        this.results = results;
    }

    public List<ModelLoadResult> getResults()
    {
        return results;
    }

    public void setShouldCentre(boolean shouldCentre)
    {
        this.shouldCentre = shouldCentre;
    }

    public boolean isShouldCentre()
    {
        return shouldCentre;
    }

}
