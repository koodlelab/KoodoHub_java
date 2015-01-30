package com.koodohub.resource;

import com.google.common.base.Optional;
import com.koodohub.domain.*;
import com.koodohub.service.MailService;
import com.koodohub.service.UserService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import javafx.collections.transformation.SortedList;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.*;

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
    private SecureRandom secureRandom = new SecureRandom();

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
        logger.info("creating member {}", userEntry.getUsername());
        if (userService.getUserByEmail(userEntry.getEmail()).isPresent()) {
            return new ErrorResponse(Response.Status.CONFLICT,
                    userEntry.getEmail()+" has been registered.").build();
        }
        if (userService.getUserByUsername(userEntry.getUsername()).isPresent()) {
            return new ErrorResponse(Response.Status.CONFLICT,
                    userEntry.getUsername()+" has been used.").build();
        }
        try {
            User user = userService.createUser(userEntry.getUsername(), userEntry.getPassword(),
                    userEntry.getFullname(), userEntry.getEmail());
            logger.info("member {} created.", user.getFullname());
            mailService.sendActivationEmail(getBaseUriRoutingString(), user.getEmail(), user.getUsername(), user.getActivationKey());
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
    @Path("/activateAccount/{email}/{token}")
    @UnitOfWork
    public Response activateAccount(@PathParam("email") String email, @PathParam("token") String token) {
        logger.debug("activate account for {}", email);
        return Optional.fromNullable(userService.activateUser(email, token))
                    .transform(user -> new SuccessResponse(Response.Status.ACCEPTED,
                            user.get().getFullname() + ", your account is activated.  Please sign in.", null).build())
                    .or(new ErrorResponse(Response.Status.BAD_REQUEST,
                            "Invalid activation.").build());
    }

    @GET
    @Path("/{username}")
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
                    user.getUsername(),
                    e);
            return new ErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    "Unable to upload picture.").build();
        }
    }

    @POST
    @Path("/uploadCover")
    @UnitOfWork
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadCover(@Auth User user,
                                 @FormDataParam("file") final InputStream uploadStream,
                                 @FormDataParam("file") FormDataContentDisposition fileDetail) {
        String newFileName = "P"+secureRandom.nextInt()+"_"+fileDetail.getFileName();
        java.nio.file.Path outputPath = FileSystems.getDefault().getPath("media/covers/", newFileName);
        try {
            Files.copy(uploadStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
            user.setCoverLink("covers/" + newFileName);
            return new SuccessResponse(Response.Status.OK,
                    "Cover is changed.", null).build();
        } catch (IOException e) {
            logger.error("Failed to save file {} for user {}",
                    fileDetail.getFileName(),
                    user.getUsername(),
                    e);
            return new ErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    "Unable to upload picture.").build();
        }
    }

    @GET
    @UnitOfWork
    @Path("/updateEmail")
    public Response updateEmail(@Auth User user,
            @QueryParam("email") String email) {
        logger.info("update user {}", user.getUsername());
        user.setEmail(email);
        return new SuccessResponse(Response.Status.OK,
                "Email is updated.", null).build();
    }

    @GET
    @UnitOfWork
    @Path("/updatePassword")
    public Response updatePassword(@Auth User user,
                                @QueryParam("oldPassword") String oldPassword,
                                @QueryParam("newPassword") String newPassword) {
        logger.info("update user {}", user.getUsername());
        //TODO validation of old password if password is changed.
        user.updatePassword(newPassword);
        return new SuccessResponse(Response.Status.OK,
                "Password is updated.", null).build();
    }
    @GET
    @Path("/getFavorites")
    @UnitOfWork
    public List<Project> getFavoriteProjects(@Auth User user) {
        Hibernate.initialize(user.getFavorites());
        List<Favorite> favorites = user.getFavorites();
        List<Project> projects = new ArrayList<>(favorites.size());
        for (final Favorite favorite : favorites) {
            projects.add(favorite.getProject());
        }
        Collections.sort(projects, new Comparator<Project>() {
            @Override
            public int compare(Project o1, Project o2) {
                return o1.getCreatedOn().before(o2.getCreatedOn()) ? 1 : -1;
            }
        });
        return projects;
    }

    @GET
    @Path("/getProjects")
    @UnitOfWork
    public List<Project> getUserProjects(@Auth User user,
                                        @QueryParam("username") String username,
                                        @QueryParam("includeFollowing") boolean includeFollowing) {
        Optional<User> userQuery = userService.getUserByUsername(username);
        if (userQuery.isPresent()) {
            List<Project> projects = userQuery.get().getProjects();
            if (includeFollowing) {
                List<User> users = userService.getFollowingsByUser(username);
                for (User following : users) {
                    projects.addAll(following.getProjects());
                }
            }
            Collections.sort(projects, new Comparator<Project>() {
                @Override
                public int compare(Project o1, Project o2) {
                    return o1.getCreatedOn().before(o2.getCreatedOn()) ? 1 : -1;
                }
            });
            logger.debug("querying projects by username:{} size:{}", username, projects.size());
            return projects;
        } else {
            return Collections.emptyList();
        }
    }

    @GET
    @Path("/follow")
    @UnitOfWork
    public Response followUser(@Auth User user, @QueryParam("username") String username) {
        Optional<User> userQuery = userService.getUserByUsername(username);
        if (userQuery.isPresent()) {
            userService.followUser(user, userQuery.get());
            List<User> users = userService.getFollowersByUser(username);
            return new SuccessResponse<List<User>>(Response.Status.OK,
                    "You are now following "+username, users).build();
        } else {
            return new ErrorResponse(Response.Status.BAD_REQUEST,
                    username+" does not exist.", null).build();
        }
    }

    @GET
    @Path("/unfollow")
    @UnitOfWork
    public Response unfollowUser(@Auth User user, @QueryParam("username") String username) {
        Optional<User> userQuery = userService.getUserByUsername(username);
        if (userQuery.isPresent()) {
            userService.unfollowUser(user, userQuery.get());
            List<User> users = userService.getFollowersByUser(username);
            return new SuccessResponse<List<User>>(Response.Status.OK,
                    "You have un-followed "+username, users).build();
        } else {
            return new ErrorResponse(Response.Status.BAD_REQUEST,
                    username+" does not exist.", null).build();
        }
    }

    @GET
    @Path("/getFollowers")
    @UnitOfWork
    public List<User> getFollowers(@Auth User user, @QueryParam("username") String username) {
        List<User> users = userService.getFollowersByUser(username);
        logger.debug("get followers for user {} size {}", username, users.size());
        return users;
    }

    @GET
    @Path("/getFollowings")
    @UnitOfWork
    public List<User> getFollowings(@Auth User user, @QueryParam("username") String username) {
        List<User> users = userService.getFollowingsByUser(username);
        logger.debug("get followings for user {} size {}", username, users.size());
        return users;
    }
}
