package com.koodohub.domain;

import com.codahale.metrics.MetricRegistryListener;
import com.fasterxml.jackson.annotation.JsonView;
import com.koodohub.security.JsonViews;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.sql.Date;

@Embeddable
public class AuditUpdate {

    @Column(name = "createdon", nullable = false)
    @JsonView(JsonViews.Project.class)
    private Date createdOn;

    @Column(name = "updatedon", nullable = false)
    private Date updatedOn;

    protected void init() {
        this.createdOn = this.updatedOn = new Date(System.currentTimeMillis());
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

}
