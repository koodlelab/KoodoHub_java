package com.koodohub.resources;

import com.google.common.base.Optional;
import com.koodohub.domain.ErrorResponse;
import com.koodohub.domain.User;
import com.koodohub.domain.UserEntry;
import com.koodohub.jdbc.UserDAO;
import com.koodohub.security.Authorities;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Path("/members")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDAO dao;
    private final Logger logger = LoggerFactory.getLogger(UserResource.class);

    public UserResource(UserDAO dao) {
        this.dao = dao;
    }

    @POST
    @UnitOfWork
    public Response create(@Valid UserEntry userEntry) {
        logger.info("creating member {} {}", userEntry.getFullName(), userEntry.getUserName());
        List<String> errors = new ArrayList<String>();
        if (dao.findByEmail(userEntry.getEmail()) != null) {
            return new ErrorResponse(Response.Status.CONFLICT,
                    userEntry.getEmail()+" has been registered.").build();
        }
        if (dao.findByUsername(userEntry.getUserName()) != null) {
            return new ErrorResponse(Response.Status.CONFLICT,
                    userEntry.getUserName()+" has been used.").build();
        }
        User newUser = new User(userEntry.getFullName(), userEntry.getEmail(),
                userEntry.getPassword(), userEntry.getUserName(), Authorities.ROLE_MEMBER);
        dao.create(newUser);
        URI location = UriBuilder.fromPath(newUser.getUserName().toLowerCase()).build();
        return Response.created(location).build();
    }

    @GET
    @Path("/{username}")
    @UnitOfWork
    public Response show(@PathParam("username") String username) {
        final Optional<User> user = dao.findByUsername(username);
        if (!user.isPresent()) {
            return new ErrorResponse(Response.Status.NOT_FOUND,
                    username+" not found.").build();
        }
        return Response.status(Response.Status.OK).entity(user.get()).build();
    }
}
