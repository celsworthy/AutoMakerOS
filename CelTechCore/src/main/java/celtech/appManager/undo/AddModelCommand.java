/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import celtech.appManager.Project;
import celtech.modelcontrol.ProjectifiableThing;
import java.util.HashSet;
import java.util.Set;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author tony
 */
public class AddModelCommand extends Command
{

    private final Stenographer steno = StenographerFactory.getStenographer(
            AddModelCommand.class.getName());

    Project project;
    // by keeping the ProjectifiableThing we keep the modelId which is essential for subsequent
    // transform redos to work.
    ProjectifiableThing modelContainer;

    public AddModelCommand(Project project, ProjectifiableThing modelContainer)
    {
        this.project = project;
        this.modelContainer = modelContainer;
    }

    @Override
    public void do_()
    {
        project.addModel(modelContainer);
    }

    @Override
    public void undo()
    {
//        modelContainer.clearElements();
        Set<ProjectifiableThing> modelContainers = new HashSet<>();
        modelContainers.add(modelContainer);
        project.removeModels(modelContainers);
    }

    @Override
    public void redo()
    {
        do_();
//        //TODO ensure that user does not try to undo/redo while this is still loading
//        List<File> modelFiles = new ArrayList<>();
//        modelFiles.add(modelContainer.getModelFile());
//        ModelLoaderTask modelLoaderTask = new ModelLoaderTask(modelFiles);
//        modelLoaderTask.setOnSucceeded((WorkerStateEvent event) ->
//        {
//            //Mesh-only operation
//            ModelLoadResults modelLoadResults = modelLoaderTask.getValue();
//
//            ModelLoadResult modelLoadResult = (ModelLoadResult) modelLoadResults.getResults().get(0);
//            Set<ProjectifiableThing> loadedProjectifiableThings = modelLoadResult.getProjectifiableThings();
//            for (ProjectifiableThing loadedProjectifiableThing : loadedProjectifiableThings)
//            {
//                modelContainer.addChildNodes(loadedProjectifiableThing.getChildNodes());
//            }
//            project.addModel(modelContainer);
//
//        });
//        modelLoaderTask.setOnFailed((WorkerStateEvent event) ->
//        {
//            steno.error("Unable to re-add the model");
//        });
//        Thread th = new Thread(modelLoaderTask);
//        th.setDaemon(true);
//        th.start();
    }

    @Override
    public boolean canMergeWith(Command command)
    {
        return false;
    }

    @Override
    public void merge(Command command)
    {
        throw new UnsupportedOperationException("Should never be called.");
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().toGenericString());
        sb.append("\n");
        sb.append("Project: ");
        sb.append(project.getProjectName());
        sb.append("\n");
        sb.append("ModelContainer: ");
        sb.append(modelContainer.getModelName());
        return sb.toString();
    }
}
