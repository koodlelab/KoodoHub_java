package com.koodohub.service;

import com.google.common.base.Optional;
import com.koodohub.domain.Project;
import com.koodohub.jdbc.ProjectDAO;

public class ProjectService {

    private final ProjectDAO projectDAO;

    public ProjectService(final ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public Project createProject(final String title, final String description,
                                 final String owner, final String mediaLink) {
        Project project = new Project();
        project.init(title, description, owner, mediaLink);
        projectDAO.save(project);
        return project;
    }

    public Optional<Project> getProjectById(final int id) {
        return projectDAO.findById(id);
    }
}
