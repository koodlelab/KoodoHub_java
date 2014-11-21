package com.koodohub.resources;

import com.koodohub.domain.ErrorResponse;
import com.koodohub.domain.User;
import com.koodohub.dao.UserDAO;
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
    public Response create(@Valid User user) {
        logger.info("creating member {} {}", user.getFullName(), user.getUserName());
        List<String> errors = new ArrayList<String>();
        if (dao.findByEmail(user.getEmail()) != null) {
            return new ErrorResponse(Response.Status.CONFLICT,
                    user.getEmail()+" has been registered.").build();
        }
        if (dao.findByUsername(user.getUserName()) != null) {
            return new ErrorResponse(Response.Status.CONFLICT,
                    user.getUserName()+" has been used.").build();
        }
        user.prepareForSave();
        dao.create(user.getFullName(), user.getEmail(), user.getPassword(), user.getUserName(),
            user.getRole()  , user.getCreatedOn(), user.getUpdatedOn());
        URI location = UriBuilder.fromPath(user.getUserName().toLowerCase()).build();
        return Response.created(location).build();
    }

    @GET
    @Path("/{username}")
    public Response show(@PathParam("username") String username) {
        User user = dao.findByUsername(username);
        if (user == null) {
            return new ErrorResponse(Response.Status.NOT_FOUND,
                    user.getUserName()+" not found.").build();
        }
        return Response.status(Response.Status.OK).entity(user).build();
    }
}
