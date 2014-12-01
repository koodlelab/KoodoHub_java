package com.koodohub.resources;

import com.google.common.base.Optional;
import com.koodohub.jdbc.UserDAO;
import com.koodohub.domain.User;
import com.koodohub.security.TokenUtils;
import com.koodohub.security.UserRoles;
import com.koodohub.security.UserToken;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.ws.rs.*;
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
    @UnitOfWork
    @Path("authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    public UserToken authenticate(@FormParam("loginName") String loginName,
                           @FormParam("password") String password) {
        logger.info("{} login.", loginName);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginName, password);
        Authentication authentication = this.authManager.authenticate(authenticationToken);
        return getUserToken(loginName, authentication);
    }

    private UserToken getUserToken(String loginName, Authentication authentication) {
        Optional<User> userDetails = dao.findByLogin(loginName);

        Map<String, Boolean> roles = createRoleMap(authentication);

        return new UserToken(userDetails.get().getUserName(), roles,
                TokenUtils.createToken(userDetails.get()));
    }

    private Map<String, Boolean> createRoleMap(Authentication authentication) {
        Map<String, Boolean> roles = new HashMap<String, Boolean>();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            roles.put(authority.toString(), Boolean.TRUE);
        }
        return roles;
    }

    /**
     * Retrieves the currently logged in user.
     *
     * @return A transfer containing the username and the roles.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserRoles getUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = (String)authentication.getPrincipal();
        if (userName.equals("anonymousUser")) {
            throw new WebApplicationException(401);
        }
        return new UserRoles(userName, this.createRoleMap(authentication));
    }
}
