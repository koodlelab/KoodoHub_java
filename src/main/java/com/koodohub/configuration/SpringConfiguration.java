package com.koodohub.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class SpringConfiguration {

    @NotEmpty
    @JsonProperty("applicationContext")
    protected String[] applicationContext;

    public String[] getApplicationContext() {

        return applicationContext;
    }

    public void setApplicationContext(String[] contextFiles){

        this.applicationContext = contextFiles;
    }

}
