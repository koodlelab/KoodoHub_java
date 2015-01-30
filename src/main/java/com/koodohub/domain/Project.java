package com.koodohub.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.koodohub.security.JsonViews;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@NamedQueries({
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

    @ManyToOne
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy="project",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<Comment>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy="project",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Favorite> favorites = new ArrayList<Favorite>();

    @Transient
    @JsonView(JsonViews.Project.class)
    private int favoriteCount;

    @Transient
    @JsonView(JsonViews.Project.class)
    private int commentCount;

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

    public List<Comment> getComments() {
        return this.comments;
    }

    public List<Favorite> getFavorites() {
        return this.favorites;
    }

    public Date getCreatedOn() {
        return auditUpdate.getCreatedOn();
    }

    public int getFavoriteCount() {
        this.favoriteCount = getFavorites().size();
        return this.favoriteCount;
    }

    public int getCommentCount() {
        this.commentCount = getComments().size();
        return this.commentCount;
    }

}
