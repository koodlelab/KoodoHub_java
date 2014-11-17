package com.koodohub.resources;

import com.koodohub.core.ErrorResponse;
import com.koodohub.core.Member;
import com.koodohub.jdbi.MemberDAO;
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
public class MemberResource {

    private final MemberDAO dao;
    private final Logger logger = LoggerFactory.getLogger(MemberResource.class);

    public MemberResource(MemberDAO dao) {
        this.dao = dao;
    }

    @POST
    public Response create(@Valid Member member) {
        logger.info("creating member {} {}", member.getFullName(), member.getUserName());
        List<String> errors = new ArrayList<String>();
        if (dao.findByEmail(member.getEmail()) != null) {
            return new ErrorResponse(Response.Status.CONFLICT,
                    member.getEmail()+" has been registered.").build();
        }
        if (dao.findByUsername(member.getUserName()) != null) {
            return new ErrorResponse(Response.Status.CONFLICT,
                    member.getUserName()+" has been used.").build();
        }
        member.encryptPassword();
        dao.create(member);
        URI location = UriBuilder.fromPath(member.getUserName().toLowerCase()).build();
        return Response.created(location).build();
    }

    @GET
    @Path("/{username}")
    public Response show(@PathParam("username") String username) {
        Member user = dao.findByUsername(username);
        if (user == null) {
            return new ErrorResponse(Response.Status.NOT_FOUND,
                    user.getUserName()+" not found.").build();
        }
        return Response.status(Response.Status.OK).entity(user).build();
    }

}
