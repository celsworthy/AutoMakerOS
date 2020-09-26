package celtech.appManager;

import celtech.Lookup;
import celtech.configuration.ApplicationConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ProjectManager implements Savable, Serializable
{
    
    private static ProjectManager instance = null;
    private static List<Project> openProjects = new ArrayList<>();
    private final static String projectFileName = "projects.dat";
    private final static Stenographer steno = StenographerFactory.getStenographer(
            ProjectManager.class.getName());
    private final static ProjectFileFilter fileFilter = new ProjectFileFilter();
    
    private ProjectManager()
    {
    }
    
    public static ProjectManager getInstance()
    {
        if (instance == null)
        {
            ProjectManager pm = loadState();
            if (pm != null)
            {
                instance = pm;
            } else
            {
                instance = new ProjectManager();
            }
        }
        
        return instance;
    }
    
    private static ProjectManager loadState()
    {
        ProjectManager pm = null;
        
        Path projectPath = Paths.get(ApplicationConfiguration.getProjectDirectory());
        if (!Files.exists(projectPath))
        {
            try
            {
                Files.createDirectories(projectPath);
            } catch (IOException ex)
            {
                ex.printStackTrace();
                steno.error("Failed to create project directory");
            }
        }
        
        Path projectDataFilePath = Paths.get(ApplicationConfiguration.getProjectDirectory() + projectFileName);
        
        if (Files.exists(projectDataFilePath))
        {
            try
            {
                steno.debug("load project manager from " + ApplicationConfiguration.getProjectDirectory() + projectFileName);
                FileInputStream projectFile = new FileInputStream(ApplicationConfiguration.getProjectDirectory() + projectFileName);
                ObjectInputStream reader = new ObjectInputStream(projectFile);
                pm = new ProjectManager();
                int numberOfOpenProjects = reader.readInt();
                for (int counter = 0; counter < numberOfOpenProjects; counter++)
                {
                    String projectPathData = reader.readUTF();
                    Project project = loadProject(projectPathData);
                    if (project != null)
                    {
                        pm.projectOpened(project);
                    } else
                    {
                        steno.warning("Project Manager tried to load " + projectPathData
                                + " but it couldn't be opened");
                    }
                }
                reader.close();
            } catch (Exception ex)
            {
                ex.printStackTrace();
                steno.error("Failed to load project manager: " + ex);
            }
        }
        
        return pm;
    }
    
    public static Project loadProject(String projectPath)
    {
        String basePath = projectPath.substring(0, projectPath.lastIndexOf('.'));
        return Project.loadProject(basePath);
    }
    
    @Override
    public boolean saveState()
    {
        boolean savedSuccessfully = false;
        
        try
        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                    ApplicationConfiguration.getProjectDirectory() + projectFileName));
            
            int numberOfProjectsWithModels = 0;
            for (Project candidateProject : openProjects)
            {
                if (candidateProject.getNumberOfProjectifiableElements() > 0)
                {
                    numberOfProjectsWithModels++;
                }
            }
            
            out.writeInt(numberOfProjectsWithModels);
            
            for (Project project : openProjects)
            {
                if (project.getNumberOfProjectifiableElements() > 0)
                {
                    out.writeUTF(project.getAbsolutePath());
                }
            }
            out.close();
        } catch (FileNotFoundException ex)
        {
            steno.error("Failed to save project state");
        } catch (IOException ex)
        {
            steno.error("Couldn't write project manager state to file");
        }
        
        return savedSuccessfully;
    }
    
    public void projectOpened(Project project)
    {
        if (!openProjects.contains(project))
        {
            openProjects.add(project);
        }
    }
    
    public void projectClosed(Project project)
    {
        project.close();
        openProjects.remove(project);
        Lookup.removeProjectReferences(project);
    }
    
    public List<Project> getOpenProjects()
    {
        return openProjects;
    }
    
    private Set<String> getAvailableProjectNames()
    {
        Set<String> availableProjectNames = new HashSet<>();
        
        File projectDir = new File(ApplicationConfiguration.getProjectDirectory());
        File[] projectFiles = projectDir.listFiles(fileFilter);
        for (File file : projectFiles)
        {
            String[] fileNameElements = file.getAbsolutePath().split(escapeJava(File.separator));
            String fileName = fileNameElements[fileNameElements.length - 1];
            String projectName = fileName.substring(0, fileName.length() - 6);
            availableProjectNames.add(projectName);
        }
        return availableProjectNames;
    }
    
    public Set<String> getOpenAndAvailableProjectNames()
    {
        Set<String> openAndAvailableProjectNames = new HashSet<>();
        for (Project project : openProjects)
        {
            openAndAvailableProjectNames.add(project.getProjectName());
        }
        openAndAvailableProjectNames.addAll(getAvailableProjectNames());
        return openAndAvailableProjectNames;
    }
    
    public Optional<Project> getProjectIfOpen(String projectName)
    {
        return openProjects.stream()
                           .filter((p)-> 
                           {
                               return p.getProjectName().equals(projectName);
                           })
                           .findAny();
    }
}
