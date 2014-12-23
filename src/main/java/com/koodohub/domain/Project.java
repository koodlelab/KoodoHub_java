package com.koodohub.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @Column(name = "id")
    private int id;

    @FormParam("title")
    @Size(min=1, max=255)
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "email", nullable = false)
    private String owner;

    @Size(min = 6, max = 100)
    @Column(name = "medialink", nullable = false)
    private String mediaLink;

    @Size(max = 255)
    @Column(name = "description", nullable = true)
    private String description;

    @Size(max = 1000)
    @Column(name = "how", nullable = true)
    private String how;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMediaLink() {
        return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHow() {
        return how;
    }

    public void setHow(String how) {
        this.how = how;
    }
}
