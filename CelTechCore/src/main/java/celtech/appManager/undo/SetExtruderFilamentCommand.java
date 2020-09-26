/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import celtech.appManager.ModelContainerProject;
import celtech.roboxbase.configuration.Filament;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author tony
 */
public class SetExtruderFilamentCommand extends Command
{

    private final Stenographer steno = StenographerFactory.getStenographer(
        SetExtruderFilamentCommand.class.getName());

    ModelContainerProject project;
    Filament filament;
    Filament previousFilament;
    private final int extruderNumber;

    public SetExtruderFilamentCommand(ModelContainerProject project, Filament filament, int extruderNumber)
    {
        this.project = project;
        this.filament = filament;
        this.extruderNumber = extruderNumber;
    }

    @Override
    public void do_()
    {
        if (extruderNumber == 0)
        {
            previousFilament = project.getExtruder0FilamentProperty().get();
        } else
        {
            previousFilament = project.getExtruder1FilamentProperty().get();
        }
        redo();
    }

    @Override
    public void undo()
    {
        if (extruderNumber == 0)
        {
            project.setExtruder0Filament(previousFilament);
        } else
        {
            project.setExtruder1Filament(previousFilament);
        }
    }

    @Override
    public void redo()
    {
        if (extruderNumber == 0)
        {
            project.setExtruder0Filament(filament);
        } else
        {
            project.setExtruder1Filament(filament);
        }
    }

    @Override
    public boolean canMergeWith(Command command)
    {
        return false;
    }

    @Override
    public void merge(Command command)
    {
        throw new UnsupportedOperationException("Should never be called");
    }

}
