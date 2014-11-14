package com.koodohub;

import com.koodohub.core.Member;
import com.koodohub.jdbi.MemberDAO;
import com.koodohub.resources.MemberResource;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.util.Map;

public class KoodoHubApplication extends Application<KoodoHubConfiguration> {

    public static void main(String[] args) throws Exception {
        new KoodoHubApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<KoodoHubConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html", "root"));
        bootstrap.addBundle(new DBIExceptionsBundle());
    }

    @Override
    public void run(KoodoHubConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().setUrlPattern("/services/*");

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");
        final MemberDAO dao = jdbi.onDemand(MemberDAO.class);
        environment.jersey().register(new MemberResource(dao));

        // Spring security integration  TODO

    }

}
