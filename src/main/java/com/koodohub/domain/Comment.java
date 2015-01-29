package com.koodohub.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.koodohub.security.JsonViews;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import java.sql.Date;

@NamedQueries({
        @NamedQuery(
                name = "Comment.findById",
                query = "from Comment c where c.id = :id"
        )
})
@Entity
@Table(name = "comments")
public class Comment {
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

    @Size(max = 255)
    @Column(name = "raw", nullable = true)
    private String raw;

    @ManyToOne
    @JoinColumn(name="replyto")
    private Comment replyto;

    @Embedded
    private final AuditUpdate auditUpdate = new AuditUpdate();

    public void init(User user, Project project, String comment, Comment replyto) {
        auditUpdate.init();
        this.project = project;
        this.raw = comment;
        this.user = user;
        this.replyto = replyto;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public int getId() {
        return this.id;
    }

    public Date getCreatedOn() {
        return auditUpdate.getCreatedOn();
    }

}
