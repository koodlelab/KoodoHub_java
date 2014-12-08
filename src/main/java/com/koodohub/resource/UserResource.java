package com.koodohub.resource;

import com.google.common.base.Optional;
import com.koodohub.domain.ErrorResponse;
import com.koodohub.domain.User;
import com.koodohub.jdbc.UserDAO;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Path("/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDAO dao;
    private final Logger logger = LoggerFactory.getLogger(UserResource.class);

//    private final VelocityEngine velocityEngine = new VelocityEngine();
//    private final ;

    public UserResource(UserDAO dao) {
        this.dao = dao;
//        this.velocityEngine
    }

    @POST
    @UnitOfWork
    public Response create(@Valid User userEntry) {
        logger.info("creating member {}", userEntry.getUserName());
        if (dao.findByEmail(userEntry.getEmail()).isPresent()) {
            return new ErrorResponse(Response.Status.CONFLICT,
                    userEntry.getEmail()+" has been registered.").build();
        }
        if (dao.findByUsername(userEntry.getUserName()).isPresent()) {
            return new ErrorResponse(Response.Status.CONFLICT,
                    userEntry.getUserName()+" has been used.").build();
        }
        User newUser = new User();
        newUser.init(userEntry.getFullName(), userEntry.getEmail(),
                userEntry.getPassword(), userEntry.getUserName(), "MEMBER");
        dao.create(newUser);
        logger.info("member {} created.", newUser.getUserName());
        return Response.status(Response.Status.CREATED).build();
  }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public User show(@Auth User user, @PathParam("username") String username) {
        logger.debug("querying user information:{}", username);
        final Optional<User> userInfo = dao.findByUsername(username);
        if (!userInfo.isPresent()) {
            throw new WebApplicationException(404);
        }
        return userInfo.get();
    }

//    private String createHtmlContentFromTemplate(final User user, final Locale locale, final HttpServletRequest request,
//                                                 final HttpServletResponse response) {
//        Map<String, Object> variables = new HashMap<>();
//        variables.put("user", user);
//        variables.put("baseUrl", request.getScheme() + "://" +   // "http" + "://
//                request.getServerName() +       // "myhost"
//                ":" + request.getServerPort());
//
////        IWebContext context = new SpringWebContext(request, response, servletContext,
////                locale, variables, applicationContext);
////        return templateEngine.process("activationEmail", context);
//    }
}
