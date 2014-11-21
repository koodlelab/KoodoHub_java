package com.koodohub.security;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.util.EnumMap;

import static org.springframework.security.core.context.SecurityContextHolder.*;

public class KoodoAuthenticationManager implements AuthenticationManager {

    private Multimap<String,GrantedAuthority> privs = ArrayListMultimap.create();

    @PostConstruct
    public void init() {
        privs.put(Authorities.ROLE_MEMBER, new SimpleGrantedAuthority(Authorities.ROLE_MEMBER));
        privs.put(Authorities.ROLE_ADMIN, new SimpleGrantedAuthority(Authorities.ROLE_ADMIN));
        privs.put(Authorities.ROLE_ADMIN, new SimpleGrantedAuthority(Authorities.ROLE_MEMBER));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        return null;
        String user = String.valueOf(authentication.getName());
        String password = String.valueOf(authentication.getCredentials());
//
//
//        if (!privs.containsKey(user) || !"test".equals(password)) {
//            throw new BadCredentialsException("Access denied.");
//        }

        //return authentication token + set roles in context
        Authentication auth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                authentication.getCredentials(), privs.get(Authorities.ROLE_MEMBER));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return auth;


//        if (!privs.containsKey(user) || !"test".equals(password)) {
//            throw new BadCredentialsException("Access denied.");
//        }


        //return authentication token + set roles in context
//        Authentication auth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
//                authentication.getCredentials(), Roles.valueOf());
//        getContext().setAuthentication(auth);
//        return auth;
    }
}
