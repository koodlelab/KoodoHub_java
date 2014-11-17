package com.koodohub.resources;

import com.koodohub.core.ErrorResponse;
import com.koodohub.core.Member;
import com.koodohub.jdbi.MemberDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

@Path("/members")
@Produces(MediaType.APPLICATION_JSON)
public class MemberResource {

    private final MemberDAO dao;
    private final Logger logger = LoggerFactory.getLogger(MemberResource.class);

    public MemberResource(MemberDAO dao) {
        this.dao = dao;
    }

    @POST
    public Response create(@Valid Member member) {
        logger.info("creating member {} {}", member.getFullName(), member.getUserName());

        try {
            member.encryptPassword();
            dao.create(member);
        } catch (Exception e) {
            logger.error("Error saving member:"+member.getUserName()+" to db. Please contact site admin.", e);
            return ErrorResponse.fromException(e).build();
        }
        URI location = UriBuilder.fromPath(member.getUserName().toLowerCase()).build();
        return Response.created(location).build();
    }

    @GET
    @Path("/{username}")
    public Response show(@PathParam("username") String username) {
        Member user = dao.findByUsername(username);
        if (user == null) {
            return new ErrorResponse(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(user).build();
    }

}
