package com.koodohub.resources;

import com.google.common.base.Optional;
import com.koodohub.domain.ErrorResponse;
import com.koodohub.domain.User;
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
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDAO dao;
    private final Logger logger = LoggerFactory.getLogger(UserResource.class);

    public UserResource(UserDAO dao) {
        this.dao = dao;
    }

    @POST
    @UnitOfWork
    public Response create(@Valid User user) {
        logger.info("creating member {}", user.getUserName());
        List<String> errors = new ArrayList<String>();
        if (dao.findByEmail(user.getEmail()).isPresent()) {
            return new ErrorResponse(Response.Status.CONFLICT,
                    user.getEmail()+" has been registered.").build();
        }
        if (dao.findByUsername(user.getUserName()).isPresent()) {
            return new ErrorResponse(Response.Status.CONFLICT,
                    user.getUserName()+" has been used.").build();
        }
        User newUser = new User(user.getFullName(), user.getEmail(),
                user.getPassword(), user.getUserName(), Authorities.ROLE_MEMBER);
        dao.create(newUser);
        URI location = UriBuilder.fromPath(newUser.getUserName().toLowerCase()).build();
        return Response.created(location).build();
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public User show(@PathParam("username") String username) {
        logger.debug("querying user information:{}", username);
        final Optional<User> user = dao.findByUsername(username);
        if (!user.isPresent()) {
            throw new WebApplicationException(404);
//            return new ErrorResponse(Response.Status.NOT_FOUND,
//                    username+" not found.").build();
        }
        logger.debug("user email:"+user.get().getEmail());
        return user.get();
    }
}
