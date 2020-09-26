/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI.controllers;

import celtech.appManager.Project;

/**
 * A ProjectAwareController can be directly told which project to bind to.
 * @author tony
 */
public interface ProjectAwareController
{
    public void setProject(Project project);
    public void shutdownController();
}
