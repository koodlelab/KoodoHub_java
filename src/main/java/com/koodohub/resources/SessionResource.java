package com.koodohub.resources;

import com.koodohub.dao.UserDAO;
import com.koodohub.security.TokenUtils;
import com.koodohub.security.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/session")
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {

    private final Logger logger = LoggerFactory.getLogger(SessionResource.class);

    private AuthenticationManager authManager;

    private UserDAO dao;

    public SessionResource(final UserDAO dao, final AuthenticationManager authManager) {
        this.authManager = authManager;
        this.dao = dao;
    }

    @POST
    @Path("authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    public UserToken authenticate(@FormParam("loginName") String loginName,
                           @FormParam("password") String password) {
        logger.info("{} login.", loginName);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginName, password);
        Authentication authentication = this.authManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Map<String, Boolean> roles = new HashMap<String, Boolean>();

		/*
		 * Reload user as password of authentication principal will be null after authorization and
		 * password is needed for token generation
		 */
        UserDetails userDetails = dao.loadUserByUsername(loginName);

        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            roles.put(authority.toString(), Boolean.TRUE);
        }

        return new UserToken(userDetails.getUsername(), roles, TokenUtils.createToken(userDetails));
    }
}
