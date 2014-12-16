package com.koodohub.resource;

import com.google.common.base.Optional;
import com.koodohub.domain.ErrorResponse;
import com.koodohub.domain.SuccessResponse;
import com.koodohub.domain.User;
import com.koodohub.service.MailService;
import com.koodohub.service.UserService;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Context
    private UriInfo uri;
    private final UserService userService;
    private final MailService mailService;
    private final Logger logger = LoggerFactory.getLogger(UserResource.class);
    private String baseUri = null;

    public UserResource(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    private String getBaseUriRoutingString() {
        if (baseUri == null) {
            String uriString = uri.getBaseUri().toString();
            int endIndex = uriString.indexOf(uri.getBaseUri().getPath());
            baseUri = uriString.substring(0, endIndex);
            baseUri += "/#/"; //required by angularJS for location routing. a bit ugly
            // to have to know about front end tech.  TODO
        }
        return baseUri;
    }

    @POST
    @UnitOfWork
    public Response create(@Valid User userEntry) {
        logger.info("creating member {}", userEntry.getUserName());
        if (userService.getUserByEmail(userEntry.getEmail()).isPresent()) {
            return new ErrorResponse(Response.Status.CONFLICT,
                    userEntry.getEmail()+" has been registered.").build();
        }
        if (userService.getUserByUsername(userEntry.getUserName()).isPresent()) {
            return new ErrorResponse(Response.Status.CONFLICT,
                    userEntry.getUserName()+" has been used.").build();
        }
        User user = userService.createUser(userEntry.getUserName(), userEntry.getPassword(),
                userEntry.getFullName(), userEntry.getEmail());
        logger.info("member {} created.", user.getFullName());
        mailService.sendActivationEmail(getBaseUriRoutingString(), user.getEmail(), user.getUserName(), user.getActivationKey());
        return new SuccessResponse(Response.Status.CREATED,
                "Please check "+user.getEmail()+" to activate your account.").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/activateAccount/{email}/{token}")
    @UnitOfWork
    public Response activateAccount(@PathParam("email") String email, @PathParam("token") String token) {
        logger.debug("activate account for {}", email);
//        Optional<User> user = userService.activateUser(email, token);
//        if (user.isPresent()) {
//            return new SuccessResponse(Response.Status.ACCEPTED,
//                    user.get().getFullName()+", your account is activated.  Please sign in.").build();
//        } else {
//            return new ErrorResponse(Response.Status.BAD_REQUEST,
//                    "Invalid activation.").build();
//        }
        return Optional.fromNullable(userService.activateUser(email, token))
                    .transform(user -> new SuccessResponse(Response.Status.ACCEPTED,
                            user.get().getFullName()+", your account is activated.  Please sign in.").build())
                    .or(new ErrorResponse(Response.Status.BAD_REQUEST,
                            "Invalid activation.").build());
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public User show(@Auth User user, @PathParam("username") String username) {
        logger.debug("querying user information:{}", username);
        final Optional<User> userInfo = userService.getUserByUsername(username);
        if (!userInfo.isPresent()) {
            throw new WebApplicationException(404);
        }
        return userInfo.get();
    }

}
