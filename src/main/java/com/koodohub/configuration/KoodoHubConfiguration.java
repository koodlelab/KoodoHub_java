package com.koodohub.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class KoodoHubConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();


    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @Valid
    @NotNull
    @JsonProperty("spring")
    private SpringConfiguration springConfiguration = new SpringConfiguration();

    public SpringConfiguration getSpringConfiguration() {
        return springConfiguration;
    }
}
