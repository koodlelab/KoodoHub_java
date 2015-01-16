package com.koodohub.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.koodohub.security.JsonViews;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;

@NamedQueries({
        @NamedQuery(
                name = "Project.findByUsername",
                query = "from Project p where p.owner = :username"
        ),
        @NamedQuery(
                name = "Project.findById",
                query = "from Project p where p.id = :id"
        )
})
@Entity
@Table(name = "projects")
public class Project {
    @Id
    @Column(name = "id")
    private int id;

    @FormParam("title")
    @Size(min=1, max=255)
    @Column(name = "title", nullable = false)
    @JsonView(JsonViews.Project.class)
    private String title;

    @Column(name = "owner", nullable = false)
    private String owner;

    @Size(min=1, max = 500)
    @Column(name = "medialink", nullable = false)
    @FormParam("medialink")
    @JsonView(JsonViews.Project.class)
    private String medialink;

    @Size(max = 255)
    @Column(name = "description", nullable = true)
    @FormParam("description")
    @JsonView(JsonViews.Project.class)
    private String description;

    @Embedded
    private final AuditUpdate auditUpdate;

    public Project() {
        this.auditUpdate = new AuditUpdate();
    }

    public void init(String title, String description, String owner, String medialink) {
        auditUpdate.init();
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.medialink = medialink;
    }

    public String getTitle() {
        return title;
    }

    public String getMedialink() {
        return medialink;
    }

    public void setMedialink(String medialink) {
        this.medialink = medialink;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
