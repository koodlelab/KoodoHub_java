package com.koodohub;

import org.springframework.context.annotation.*;
import com.koodohub.jdbc.UserDAO;

/**
 Main Spring Configuration
 */
@Configuration
@ImportResource("classpath:security.xml")
@ComponentScan(basePackageClasses = KoodoHubSpringConfiguration.class)
public class KoodoHubSpringConfiguration {

}