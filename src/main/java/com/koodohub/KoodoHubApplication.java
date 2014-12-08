package com.koodohub;

import com.koodohub.domain.User;
import com.koodohub.jdbc.UserDAO;
import com.koodohub.resource.SessionResource;
import com.koodohub.resource.UserResource;
import com.koodohub.security.KoodoHubAuthProvider;
import com.koodohub.security.KoodoHubAuthenticator;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class KoodoHubApplication extends Application<KoodoHubConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(KoodoHubApplication.class);
    protected ApplicationContext applicationContext;

    private final HibernateBundle<KoodoHubConfiguration> hibernateBundle =
            new HibernateBundle<KoodoHubConfiguration>(User.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(KoodoHubConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    public static void main(String[] args) throws Exception {
        new KoodoHubApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<KoodoHubConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/web", "/", "index.html", "root"));
        bootstrap.addBundle(new MigrationsBundle<KoodoHubConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(KoodoHubConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        logger.info("Koodo Hub initialized.");
    }

    @Override
    public void run(KoodoHubConfiguration configuration, Environment environment) throws Exception {

        final UserDAO userDAO = new UserDAO(hibernateBundle.getSessionFactory());
        KoodoHubAuthenticator authenticator = new KoodoHubAuthenticator(userDAO);
        environment.jersey().setUrlPattern("/resource/*");
        // register dropwizard resource
        environment.jersey().register(new UserResource(userDAO));
        environment.jersey().register(new SessionResource(userDAO, authenticator));
        environment.jersey().register(new KoodoHubAuthProvider(authenticator));
    }

}
