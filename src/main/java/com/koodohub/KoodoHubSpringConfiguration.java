package com.koodohub;

//import com.koodohub.security.SpringSecurityConfiguration;
import org.springframework.context.annotation.*;

/**
 Main Spring Configuration
 */
@Configuration
@ImportResource("classpath:security.xml")
@ComponentScan(basePackageClasses = KoodoHubSpringConfiguration.class)
public class KoodoHubSpringConfiguration {

}