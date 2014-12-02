package com.koodohub.resources;

import com.google.common.base.Optional;
import com.koodohub.domain.User;
import com.koodohub.jdbc.UserDAO;
import com.koodohub.security.TokenUtils;
import com.koodohub.security.UserToken;
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

    private Authenticator authManager;

    private UserDAO dao;

    public SessionResource(final UserDAO dao, final Authenticator authManager) {
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
        Optional<User> user = null;
        try {
            user = this.authManager.authenticate(
                    new BasicCredentials(loginName, password));
        } catch (AuthenticationException e) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        return getUserToken(loginName, user.get());
    }

    private UserToken getUserToken(String loginName, User user) {
        Optional<User> userDetails = dao.findByLogin(loginName);
        return new UserToken(userDetails.get().getUserName(), TokenUtils.createToken(userDetails.get()));
    }
}
