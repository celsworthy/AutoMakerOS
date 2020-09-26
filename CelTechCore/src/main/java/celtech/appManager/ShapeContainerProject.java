package celtech.appManager;

import celtech.configuration.ApplicationConfiguration;
import celtech.configuration.fileRepresentation.ProjectFile;
import celtech.configuration.fileRepresentation.ShapeContainerProjectFile;
import celtech.modelcontrol.Groupable;
import celtech.modelcontrol.ModelGroup;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.roboxbase.configuration.fileRepresentation.PrinterSettingsOverrides;
import celtech.utils.threed.importers.svg.ShapeContainer;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class ShapeContainerProject extends Project
{

    private static final Stenographer steno = StenographerFactory.getStenographer(ShapeContainerProject.class.getName());

    public ShapeContainerProject()
    {
        super();
    }

    @Override
    protected void initialise()
    {
    }

    @Override
    protected void save(String basePath)
    {
    }

    @Override
    public void addModel(ProjectifiableThing projectifiableThing)
    {
        if (projectifiableThing instanceof ShapeContainer)
        {
            ShapeContainer modelContainer = (ShapeContainer) projectifiableThing;
            topLevelThings.add(modelContainer);
            projectModified();
            fireWhenModelAdded(modelContainer);
        }
    }

    private void fireWhenModelAdded(ShapeContainer modelContainer)
    {
        for (ProjectChangesListener projectChangesListener : projectChangesListeners)
        {
            projectChangesListener.whenModelAdded(modelContainer);
        }
    }

    @Override
    public void removeModels(Set<ProjectifiableThing> projectifiableThings)
    {
        Set<ShapeContainer> modelContainers = (Set) projectifiableThings;

        for (ShapeContainer modelContainer : modelContainers)
        {
            assert modelContainer != null;
        }

        topLevelThings.removeAll(modelContainers);

//        for (RenderableSVG modelContainer : modelContainers)
//        {
//            removeModelListeners(modelContainer);
//            for (RenderableSVG childModelContainer : modelContainer.getChildModelContainers())
//            {
//                removeModelListeners(childModelContainer);
//            }
//        }
        projectModified();
//        fireWhenModelsRemoved(projectifiableThings);
    }

    @Override
    public void autoLayout()
    {
    }

    @Override
    public Set<ProjectifiableThing> getAllModels()
    {
        Set<ProjectifiableThing> allModelContainers = new HashSet<>();
        for (ProjectifiableThing loadedModel : topLevelThings)
        {
            allModelContainers.add(loadedModel);
        }
        return allModelContainers;
    }

    @Override
    protected void fireWhenModelsTransformed(Set<ProjectifiableThing> projectifiableThings)
    {
        for (ProjectChangesListener projectChangesListener : projectChangesListeners)
        {
            projectChangesListener.whenModelsTransformed(projectifiableThings);
        }
    }

    @Override
    protected void fireWhenPrinterSettingsChanged(PrinterSettingsOverrides printerSettings)
    {
    }

        @Override
    protected void fireWhenTimelapseSettingsChanged(TimelapseSettingsData timelapseSettings)
    {
    }

    @Override
    protected void load(ProjectFile projectFile, String basePath) throws ProjectLoadException
    {
        suppressProjectChanged = true;

        if (projectFile instanceof ShapeContainerProjectFile)
        {
            try
            {
                projectNameProperty.set(projectFile.getProjectName());
                lastModifiedDate.set(projectFile.getLastModifiedDate());
                lastPrintJobID = projectFile.getLastPrintJobID();
                
                loadTimelapseSettings(projectFile);

                loadModels(basePath);

            } catch (IOException ex)
            {
                steno.exception("Failed to load project " + basePath, ex);
            } catch (ClassNotFoundException ex)
            {
                steno.exception("Failed to load project " + basePath, ex);
            }
        }

        suppressProjectChanged = false;
    }

    private void loadModels(String basePath) throws IOException, ClassNotFoundException
    {
        FileInputStream fileInputStream = new FileInputStream(basePath
                + ApplicationConfiguration.projectModelsFileExtension);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ObjectInputStream modelsInput = new ObjectInputStream(bufferedInputStream);
        int numModels = modelsInput.readInt();

        for (int i = 0; i < numModels; i++)
        {
            ShapeContainer modelContainer = (ShapeContainer) modelsInput.readObject();
            addModel(modelContainer);
        }
    }

    @Override
    protected void checkNotAlreadyInGroup(Set<Groupable> modelContainers)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ModelGroup createNewGroupAndAddModelListeners(Set<Groupable> modelContainers)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void autoLayout(List<ProjectifiableThing> thingsToLayout)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
