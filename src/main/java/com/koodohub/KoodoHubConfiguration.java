package com.koodohub;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;

public class KoodoHubConfiguration extends Configuration {
    private final static Logger log = LoggerFactory.getLogger(KoodoHubConfiguration.class);

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @NotNull
    @JsonProperty("emailFrom")
    private String emailFrom;


    public DataSourceFactory getDataSourceFactory() {
        String herokuDB = System.getenv("DATABASE_URL");
        if (herokuDB != null) {
            //Heroku postgresql handling
            try {
                URI dbUri = new URI(herokuDB);
                final String user = dbUri.getUserInfo().split(":")[0];
                final String password = dbUri.getUserInfo().split(":")[1];
                final String url = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
                DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration() {
                    DataSourceFactory dataSourceFactory;

                    @Override
                    public DataSourceFactory getDataSourceFactory(Configuration configuration) {
                        if (dataSourceFactory != null) {
                            return dataSourceFactory;
                        }
                        DataSourceFactory dsf = new DataSourceFactory();
                        dsf.setUser(user);
                        dsf.setPassword(password);
                        dsf.setUrl(url);
                        dsf.setDriverClass("org.postgresql.Driver");
                        dataSourceFactory = dsf;
                        return dsf;
                    }
                };
                return databaseConfiguration.getDataSourceFactory(null);
            } catch (URISyntaxException e) {
                log.error("Error handling heroku db url {}", herokuDB, e);
            }
        }
        return database;
    }

    public String getEmailFrom() {
        return emailFrom;
    }



}
