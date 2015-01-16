package com.koodohub.resource;

import com.google.common.base.Optional;
import com.koodohub.domain.ErrorResponse;
import com.koodohub.domain.SuccessResponse;
import com.koodohub.domain.User;
import com.koodohub.service.MailService;
import com.koodohub.service.UserService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
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
import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

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
        try {
            User user = userService.createUser(userEntry.getUserName(), userEntry.getPassword(),
                    userEntry.getFullName(), userEntry.getEmail());
            logger.info("member {} created.", user.getFullName());
            mailService.sendActivationEmail(getBaseUriRoutingString(), user.getEmail(), user.getUserName(), user.getActivationKey());
            return new SuccessResponse(Response.Status.CREATED,
                    "Please check " + user.getEmail() + " to activate your account.", null).build();
        } catch (Exception e) {
            logger.error("member {} creation failed.", e);
            return new ErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error creating account.", null).build();
        }
    }

    @GET
    @UnitOfWork
    public List<User> getOtherUsers(@Auth User user) {
        logger.info("get all members.");
        List<User> users = userService.getAllUsers();
        users.remove(user);
        return users;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/activateAccount/{email}/{token}")
    @UnitOfWork
    public Response activateAccount(@PathParam("email") String email, @PathParam("token") String token) {
        logger.debug("activate account for {}", email);
        return Optional.fromNullable(userService.activateUser(email, token))
                    .transform(user -> new SuccessResponse(Response.Status.ACCEPTED,
                            user.get().getFullName()+", your account is activated.  Please sign in.", null).build())
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

    @POST
    @Path("/uploadAvatar")
    @UnitOfWork
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadAvatar(@Auth User user,
            @FormDataParam("file") final InputStream uploadStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
        java.nio.file.Path outputPath = FileSystems.getDefault().getPath("media/avatars/", fileDetail.getFileName());
        try {
            Files.copy(uploadStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
            user.setAvatarLink("avatars/"+fileDetail.getFileName());
            return new SuccessResponse(Response.Status.OK,
                    "Picture is uploaded.", null).build();
        } catch (IOException e) {
            logger.error("Failed to save file {} for user {}",
                    fileDetail.getFileName(),
                    user.getUserName(),
                    e);
            return new ErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    "Unable to upload picture.").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @Path("/updateEmail")
    public Response updateEmail(@Auth User user,
            @QueryParam("email") String email) {
        logger.info("update user {}", user.getUserName());
        user.setEmail(email);
        return new SuccessResponse(Response.Status.OK,
                "Email is updated.", null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @Path("/updatePassword")
    public Response updatePassword(@Auth User user,
                                @QueryParam("oldPassword") String oldPassword,
                                @QueryParam("newPassword") String newPassword) {
        logger.info("update user {}", user.getUserName());
        //TODO validation of old password if password is changed.
        user.updatePassword(newPassword);
        return new SuccessResponse(Response.Status.OK,
                "Password is updated.", null).build();
    }

    @GET
    @Path("/getFollowers")
    public List<String> getFollowers(@QueryParam("username") String username) {
        return userService.getFollowedBy(username);
    }

    @GET
    @Path("/getFollowings")
    public List<String> getFollowings(@PathParam("username") String username) {
        return userService.getFollowings(username);
    }
}
