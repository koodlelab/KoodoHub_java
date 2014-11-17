package com.koodohub;

import org.springframework.context.annotation.*;
import org.springframework.security.access.vote.RoleVoter;

/**
 Main Spring Configuration
 */
@Configuration
//@ImportResource("classpath:myapp-security.xml")
@Import({ SpringSecurityConfiguration.class })
@ComponentScan(basePackageClasses = SpringConfiguration.class)
public class SpringConfiguration {

//    @Bean
//    public RoleVoter getRoleVoter() {
//        RoleVoter roleVoter = new RoleVoter();
//        roleVoter.setRolePrefix("ROLE _");
//        return roleVoter;
//    }
}