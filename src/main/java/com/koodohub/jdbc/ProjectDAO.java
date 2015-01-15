package com.koodohub.jdbc;

import com.google.common.base.Optional;
import com.koodohub.domain.Project;
import com.koodohub.domain.User;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

public class ProjectDAO extends AbstractDAO<Project> {

    public ProjectDAO(SessionFactory factory) {
        super(factory);
    }

    public Project save(Project project) {
        return persist(project);
    }

    public List<Project> findByUsername(String username) {
        return list(namedQuery("Project.findByUsername")
                .setString("username", username));
    }

}