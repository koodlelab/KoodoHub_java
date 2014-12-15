package com.koodohub;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class KoodoHubConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @NotNull
    @JsonProperty("activationEmailTemplate")
    private String activationEmailTemplate;

    @Valid
    @NotNull
    @JsonProperty("emailFrom")
    private String emailFrom;


    public DataSourceFactory getDataSourceFactory() {
        String herokuDB = System.getenv("DATABASE_URL");
        if (herokuDB == null) {
            return database;
        } else {
            DatabaseConfiguration databaseConfiguration = HerokuDB.create(herokuDB);
            database = databaseConfiguration.getDataSourceFactory(null);
            return database;
        }
    }

    public String getActivationEmailTemplate() {
        return activationEmailTemplate;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

}
