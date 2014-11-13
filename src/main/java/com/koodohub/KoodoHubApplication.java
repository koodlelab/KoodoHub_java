package com.koodohub;

import com.koodohub.resources.MemberResource;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
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
    }

    @Override
    public void run(KoodoHubConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().setUrlPattern("/services/*");
        final MemberResource memberResource = new MemberResource();
        environment.jersey().register(memberResource);

        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();

    }


//    @Override
//    public void initialize(Bootstrap<Configuration> bootstrap) {
//        AssetsBundle bundle = new AssetsBundle("/assets", "/", "index.html");
//        bootstrap.addBundle(bundle);
//    }
//
//    @Override
//    public void run(Configuration configuration, Environment environment) throws Exception {
//        environment.
////        environment.setJerseyServletContainer(null);
////        environment
////        environment.jersey().setUrlPattern("/services/*");
////        environment.addResource(new MemberResource());
//
////        //init Spring context
////        //before we init the app context, we have to create a parent context with all the config objects others rely on to get initialized
////        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
////        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
////
////        root.refresh();
////        root.getBeanFactory().registerSingleton("configuration",configuration);
////        root.registerShutdownHook();
////        root.start();
////
////        //the real main app context has a link to the parent context
////        ctx.setParent(root);
////        ctx.register(Configuration.class);
////        ctx.refresh();
////        ctx.registerShutdownHook();
////        ctx.start();
////
////        //now that Spring is started, let's get all the beans that matter into DropWizard
////
////        //health checks
////        Map<String, HealthCheck> healthChecks = ctx.getBeansOfType(HealthCheck.class);
////        for(Map.Entry<String,HealthCheck> entry : healthChecks.entrySet()) {
////            environment.addHealthCheck(entry.getValue());
////        }
////
////        //resources
////        Map<String, Object> resources = ctx.getBeansWithAnnotation(Path.class);
////        for(Map.Entry<String,Object> entry : resources.entrySet()) {
////            environment.addResource(entry.getValue());
////        }
////
////        //tasks
////        Map<String, Task> tasks = ctx.getBeansOfType(Task.class);
////        for(Map.Entry<String,Task> entry : tasks.entrySet()) {
////            environment.addTask(entry.getValue());
////        }
////
////        //JAX-RS providers
////        Map<String, Object> providers = ctx.getBeansWithAnnotation(Provider.class);
////        for(Map.Entry<String,Object> entry : providers.entrySet()) {
////            environment.addProvider(entry.getValue());
////        }
////
////        //last, but not least, let's link Spring to the embedded Jetty in Dropwizard
////        environment.addServletListeners(new ContextLoaderListener(ctx));
////
////        //activate Spring Security filter
//////        environment.addFilter(DelegatingFilterProxy.class,"/*").setName("springSecurityFilterChain");
//    }
}
