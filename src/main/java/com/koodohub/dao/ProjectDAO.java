package com.koodohub.dao;

import com.google.common.base.Optional;
import com.koodohub.domain.Project;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

public class ProjectDAO extends AbstractDAO<Project> {

    public ProjectDAO(SessionFactory factory) {
        super(factory);
    }

    public Project save(Project project) {
        return persist(project);
    }

//    public List<Project> findByUsername(String username) {
//        return list(namedQuery("Project.findByUsername")
//                .setString("username", username));
//    }

    public Optional<Project> findById(int id) {
        return Optional.fromNullable(uniqueResult(namedQuery("Project.findById")
                .setInteger("id", id)));
    }

}