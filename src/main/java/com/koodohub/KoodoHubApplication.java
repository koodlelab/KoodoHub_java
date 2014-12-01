package com.koodohub;

import com.codahale.metrics.health.HealthCheck;
import com.koodohub.domain.User;
import com.koodohub.jdbc.UserDAO;
import com.koodohub.resources.SessionResource;
import com.koodohub.resources.UserResource;
import com.koodohub.security.AuthenticationTokenProcessingFilter;
import com.koodohub.security.KoodoAuthenticationManager;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.util.EnumSet;
import java.util.Map;

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


        // Spring security integration
        //init Spring context
        //before we init the app context, we have to create a parent context with all the config objects others rely on to get initialized
        AnnotationConfigWebApplicationContext parent = new AnnotationConfigWebApplicationContext();
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();

        parent.refresh();
        parent.getBeanFactory().registerSingleton("configuration",configuration);
        parent.registerShutdownHook();
        parent.start();

        //the real main app context has a link to the parent context
        ctx.setParent(parent);
        ctx.register(KoodoHubSpringConfiguration.class);
        ctx.refresh();
        ctx.registerShutdownHook();
        ctx.start();

        //now that Spring is started, let's get all the beans that matter into DropWizard

        //health checks
        Map<String, HealthCheck> healthChecks = ctx.getBeansOfType(HealthCheck.class);
        for(Map.Entry<String,HealthCheck> entry : healthChecks.entrySet()) {
            environment.healthChecks().register(entry.getKey(), entry.getValue());
        }

        //resources
        Map<String, Object> resources = ctx.getBeansWithAnnotation(Path.class);
        for(Map.Entry<String,Object> entry : resources.entrySet()) {
            environment.jersey().register(entry.getValue());
        }

        //tasks
        Map<String, Task> tasks = ctx.getBeansOfType(Task.class);
        for(Map.Entry<String,Task> entry : tasks.entrySet()) {
            environment.admin().addTask(entry.getValue());
        }

        //JAX-RS providers
        Map<String, Object> providers = ctx.getBeansWithAnnotation(Provider.class);
        for(Map.Entry<String,Object> entry : providers.entrySet()) {
            environment.jersey().register(entry.getValue());
        }

        //last, but not least, let's link Spring to the embedded Jetty in Dropwizard
        //TODO
        environment.servlets().addServletListeners(new ContextLoaderListener(ctx));

        //activate Spring Security filter
        FilterRegistration.Dynamic filter =  environment.servlets()
                .addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        //inject user jdbi into authentication manager
        KoodoAuthenticationManager authenticationManager = ctx.getBean(KoodoAuthenticationManager.class);
        authenticationManager.initUserDao(userDAO);

        AuthenticationTokenProcessingFilter tokenProcessingFilter = ctx.getBean(AuthenticationTokenProcessingFilter.class);
        tokenProcessingFilter.initUserService(userDAO);

        environment.jersey().setUrlPattern("/services/*");
        // register dropwizard resources
        environment.jersey().register(new UserResource(userDAO));
        environment.jersey().register(new SessionResource(userDAO, authenticationManager));
    }

}
