package com.koodohub.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.koodohub.security.JsonViews;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import java.sql.Date;

@Entity
@Table(name = "favorites")
public class Favorite {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="username")
    private User user;

    @ManyToOne
    @JoinColumn(name="project")
    private Project project;

    @Embedded
    private final AuditUpdate auditUpdate = new AuditUpdate();

    public void init(User user, Project project) {
        auditUpdate.init();
        this.project = project;
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return this.id;
    }

    public Date getCreatedOn() {
        return auditUpdate.getCreatedOn();
    }

}
