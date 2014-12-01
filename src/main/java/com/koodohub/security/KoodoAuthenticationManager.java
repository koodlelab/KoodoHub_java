package com.koodohub.security;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.koodohub.jdbc.UserDAO;
import com.koodohub.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("KoodoAuthenticationManager")
public class KoodoAuthenticationManager implements AuthenticationManager {

    private static final Logger logger = LoggerFactory.getLogger(KoodoAuthenticationManager.class);
    private Multimap<String,GrantedAuthority> privs = ArrayListMultimap.create();
    private UserDAO userDAO;

    @PostConstruct
    public void init() {
        privs.put(Authorities.ROLE_MEMBER, new SimpleGrantedAuthority(Authorities.ROLE_MEMBER));
        privs.put(Authorities.ROLE_ADMIN, new SimpleGrantedAuthority(Authorities.ROLE_ADMIN));
        privs.put(Authorities.ROLE_ADMIN, new SimpleGrantedAuthority(Authorities.ROLE_MEMBER));
    }

    public void initUserDao(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String user = String.valueOf(authentication.getName());
        String password = String.valueOf(authentication.getCredentials());

        Optional<User> userDetails = userDAO.findByLogin(user);
        if (!userDetails.isPresent() || !userDetails.get().isCorrectPassword(password)) {
            logger.debug("login {} present? {}, password correct? {}", user, userDetails.isPresent(),
                    userDetails.get().isCorrectPassword(password));
            throw new BadCredentialsException("Access denied.");
        }
        //return authentication token + set roles in context
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails.get().getUserName(),
                authentication.getCredentials(), privs.get(Authorities.ROLE_MEMBER));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return auth;
    }
}
