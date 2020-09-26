/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI;

import celtech.appManager.Project;
import celtech.appManager.undo.CommandStack;
import celtech.coreUI.visualisation.ProjectSelection;
import celtech.modelcontrol.ModelContainer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 * The ProjectGUIState class contains GUI information for a project such as the selected models. It
 * is put here to keep the Project class clean of GUI data.
 */
public class ProjectGUIState
{
    private final ProjectSelection projectSelection;
    
    private final ObjectProperty<LayoutSubmode> layoutSubmode;
    
    private final CommandStack commandStack;
    
    private final ObservableSet<ModelContainer> excludedFromSelection = FXCollections.observableSet();
    
    private final ProjectGUIRules projectGUIRules;

    public ProjectGUIState(Project project)
    {
        projectSelection = new ProjectSelection(project);
        layoutSubmode = new SimpleObjectProperty<>(LayoutSubmode.SELECT);
        commandStack = new CommandStack();
        projectGUIRules = new ProjectGUIRules(projectSelection, excludedFromSelection);
    }

    public CommandStack getCommandStack()
    {
        return commandStack;
    }
    
    public ProjectGUIRules getProjectGUIRules() {
        return projectGUIRules;
    }
    
    public ObservableSet<ModelContainer> getExcludedFromSelection() {
        return excludedFromSelection;
    }
    
    public ProjectSelection getProjectSelection() {
        return projectSelection;
    }
    
    public ObjectProperty<LayoutSubmode> getLayoutSubmodeProperty() {
        return layoutSubmode;
    }
}
