package com.koodohub;

import com.codahale.metrics.health.HealthCheck;
import com.koodohub.configuration.KoodoHubConfiguration;
import com.koodohub.configuration.SpringConfiguration;
import com.koodohub.dao.UserDAO;
import com.koodohub.resources.SessionResource;
import com.koodohub.resources.UserResource;
import com.koodohub.security.KoodoAuthenticationManager;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.util.component.LifeCycle;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.util.EnumSet;
import java.util.Map;

public class KoodoHubApplication extends Application<KoodoHubConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(KoodoHubApplication.class);
    protected ApplicationContext applicationContext;

    public static void main(String[] args) throws Exception {
        new KoodoHubApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<KoodoHubConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/web", "/", "index.html", "root"));
        bootstrap.addBundle(new DBIExceptionsBundle());
        bootstrap.addBundle(new MigrationsBundle<KoodoHubConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(KoodoHubConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        logger.info("Koodo Hub initialized.");

    }

    @Override
    public void run(KoodoHubConfiguration configuration, Environment environment) throws Exception {

        // Populate the applicationContext based on the Spring Configuration
        initSpringConfig(configuration.getSpringConfiguration(), environment);

        // Stand up all the DropWizard Objects (from the Spring context files).
        registerManaged(environment);
        registerLifecycle(environment);
        registerTasks(environment);
        registerHealthChecks(environment);
        registerProviders(environment);
        environment.jersey().setUrlPattern("/services/*");
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");
        final UserDAO dao = jdbi.onDemand(UserDAO.class);

        environment.jersey().register(new UserResource(dao));
        AuthenticationManager authManager = applicationContext.getBean(KoodoAuthenticationManager.class);
        environment.jersey().register(new SessionResource(dao, authManager));
    }

    //TODO make it part of spring.
//    private void registerResources(Environment environment) {
//        final Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Path.class);
//
//        for (String beanName : beansWithAnnotation.keySet()) {
//
//            Object resource = beansWithAnnotation.get(beanName);
//            environment.jersey().register(resource);
//            logger.info("Registering resource : " + resource.getClass().getName());
//        }
//    }

    private void registerProviders(Environment environment) {
        final Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Provider.class);

        for (String beanName : beansWithAnnotation.keySet()) {

            Object provider = beansWithAnnotation.get(beanName);

            //environment.addProvider(provider);
            //logger.info("Registering provider : " + provider.getClass().getName());
        }
    }

    private void registerHealthChecks(Environment environment) {
        final Map<String, HealthCheck> beansOfType = applicationContext.getBeansOfType(HealthCheck.class);

        for (Map.Entry<String, HealthCheck> entry : beansOfType.entrySet()) {
            environment.healthChecks().register(entry.getKey(),entry.getValue());
            logger.info("Registering healthCheck: " + entry.getValue().getClass().getName());
        }        
    }

    private void registerTasks(Environment environment) {
        final Map<String, Task> beansOfType = applicationContext.getBeansOfType(Task.class);

        for (String beanName : beansOfType.keySet()) {

            Task task = beansOfType.get(beanName);
            environment.admin().addTask(task);

            logger.info("Registering task: " + task.getClass().getName());
        }
    }

    private void registerLifecycle(Environment environment) {
        Map<String, LifeCycle> beansOfType = applicationContext.getBeansOfType(LifeCycle.class);

        for (String beanName : beansOfType.keySet()) {

            LifeCycle lifeCycle = beansOfType.get(beanName);

            environment.lifecycle().manage(lifeCycle);

            logger.info("Registering lifeCycle: " + lifeCycle.getClass().getName());
        }        
    }

    private void registerManaged(Environment environment) {
        final Map<String, Managed> beansOfType = applicationContext.getBeansOfType(Managed.class);

        for (String beanName : beansOfType.keySet()) {

            Managed managed = beansOfType.get(beanName);
            environment.lifecycle().manage(managed);

            logger.info("Registering managed: " + managed.getClass().getName());
        }
    }

    private void initSpringConfig(SpringConfiguration springConfiguration, Environment environment) {

        applicationContext = new ClassPathXmlApplicationContext(springConfiguration.getApplicationContext());

        final XmlWebApplicationContext wctx = new XmlWebApplicationContext();
        wctx.setParent(applicationContext);
        wctx.setConfigLocation("");
        wctx.refresh();
        environment.servlets().addServletListeners(new ServletContextListener() {
            @Override
            public void contextInitialized(ServletContextEvent servCtx) {
                servCtx.getServletContext()
                        .setAttribute(
                                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                                wctx);
                wctx.setServletContext(servCtx.getServletContext());
            }

            @Override
            public void contextDestroyed(ServletContextEvent arg0) {
                // TODO Auto-generated method stub
            }
        });

        FilterRegistration.Dynamic filter =  environment.servlets()
                .addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
//        // Register the Spring Security Auth Provider
//        new SpringSecurityAuthProvider(applicationContext)
//                .registerProvider(environment);
    }



//        environment.jersey().setUrlPattern("/services/*");
//
//        final DBIFactory factory = new DBIFactory();
//        final DBI dao = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");
//        final UserDAO dao = dao.onDemand(UserDAO.class);
//
//
//        // Spring security integration  TODO
//        //init Spring context
//        //before we init the app context, we have to create a parent context with all the config objects others rely on to get initialized
//        AnnotationConfigWebApplicationContext parent = new AnnotationConfigWebApplicationContext();
//        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
//
//        parent.refresh();
//        parent.getBeanFactory().registerSingleton("configuration",configuration);
//        parent.registerShutdownHook();
//        parent.start();
//
//        //the real main app context has a link to the parent context
//        ctx.setParent(parent);
//        ctx.register(KoodoHubSpringConfiguration.class);
//        ctx.refresh();
//        ctx.registerShutdownHook();
//        ctx.start();
//
//        //now that Spring is started, let's get all the beans that matter into DropWizard
//
//        //health checks
//        Map<String, HealthCheck> healthChecks = ctx.getBeansOfType(HealthCheck.class);
//        for(Map.Entry<String,HealthCheck> entry : healthChecks.entrySet()) {
//            environment.healthChecks().register(entry.getKey(), entry.getValue());
//        }
//
//        //resources
//        Map<String, Object> resources = ctx.getBeansWithAnnotation(Path.class);
//        for(Map.Entry<String,Object> entry : resources.entrySet()) {
//            environment.jersey().register(entry.getValue());
//        }
//
//        //tasks
//        Map<String, Task> tasks = ctx.getBeansOfType(Task.class);
//        for(Map.Entry<String,Task> entry : tasks.entrySet()) {
//            environment.admin().addTask(entry.getValue());
//        }
//
//        //JAX-RS providers
//        Map<String, Object> providers = ctx.getBeansWithAnnotation(Provider.class);
//        for(Map.Entry<String,Object> entry : providers.entrySet()) {
//            environment.jersey().register(entry.getValue());
//        }
//
//        //last, but not least, let's link Spring to the embedded Jetty in Dropwizard
//        //TODO
//        environment.servlets().addServletListeners(new ContextLoaderListener(ctx));
//
//        //activate Spring Security filter
//        FilterRegistration.Dynamic filter =  environment.servlets()
//                .addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
//        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
//
//        environment.jersey().register(new UserResource(dao));
//        environment.jersey().register(new SessionResource(dao));
//    }

}
