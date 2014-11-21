package com.koodohub;

import org.springframework.context.annotation.*;

/**
 Main Spring Configuration
 */
@Configuration
@ImportResource("classpath:security.xml")
@ComponentScan(basePackageClasses = KoodoHubSpringConfiguration.class)
public class KoodoHubSpringConfiguration {

}