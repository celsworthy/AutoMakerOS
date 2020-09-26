/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.components;

/**
 *
 * @author Ian
 */
public class ProjectNotLoadedException extends Exception
{
    private String projectName = null;

    /**
     *
     * @param projectName
     */
    public ProjectNotLoadedException(String projectName)
    {
        this.projectName = projectName;
    }

    /**
     *
     * @return
     */
    @Override
    public String getMessage()
    {
        return "Project " + projectName + " could not be loaded.";
    }
}
