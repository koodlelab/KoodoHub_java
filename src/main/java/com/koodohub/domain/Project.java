package com.koodohub.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.koodohub.security.JsonViews;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import java.sql.Date;

@NamedQueries({
//        @NamedQuery(
//                name = "Project.findByUsername",
//                query = "from Project p where p.owner = :username"
//        ),
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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @FormParam("title")
    @Size(min=1, max=255)
    @Column(name = "title", nullable = false)
    @JsonView(JsonViews.Project.class)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="username")
    private User user;

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
    private final AuditUpdate auditUpdate = new AuditUpdate();

    public void init(String title, String description, User user, String medialink) {
        auditUpdate.init();
        this.title = title;
        this.description = description;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public Date getCreatedOn() {
        return auditUpdate.getCreatedOn();
    }

}
