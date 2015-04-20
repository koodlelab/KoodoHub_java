package com.koodohub.resource;

import com.google.common.base.Optional;
import com.koodohub.domain.User;
import com.koodohub.security.TokenUtils;
import com.koodohub.security.UserToken;
import com.koodohub.service.UserService;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/session")
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {

    private final Logger logger = LoggerFactory.getLogger(SessionResource.class);

    private Authenticator<BasicCredentials, User> authManager;

    private final UserService userService;

    public SessionResource(final UserService userService,
                           final Authenticator<BasicCredentials, User> authManager) {
        this.authManager = authManager;
        this.userService = userService;
    }

    @POST
    @UnitOfWork
    @Path("authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    public UserToken authenticate(@FormParam("loginName") String loginName,
                           @FormParam("password") String password) {
        logger.info("{} login.", loginName);
        Optional<User> user;
        try {
            user = this.authManager.authenticate(
                    new BasicCredentials(loginName, password));
        } catch (AuthenticationException e) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        return getUserToken(loginName, user.get());
    }

    private UserToken getUserToken(String loginName, User user) {
        Optional<User> userDetails = userService.getUserByLogin(loginName);
        return new UserToken(userDetails.get().getUsername(), TokenUtils.createToken(userDetails.get()));
    }
}
