package com.koodohub.domain;

import com.codahale.metrics.MetricRegistryListener;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.sql.Date;

@Embeddable
public class AuditUpdate {

    @Column(name = "createdon", nullable = false)
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
