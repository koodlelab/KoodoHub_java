package com.koodohub.resources;

import com.koodohub.core.ErrorResponse;
import com.koodohub.core.Member;
import com.koodohub.jdbi.MemberDAO;

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

    public MemberResource(MemberDAO dao) {
        this.dao = dao;
    }

    @POST
    public Response create(@FormParam("name") String name,
                           @FormParam("email") String email,
                           @FormParam("password") String password,
                           @FormParam("password_confirmation") String password_confirmation) {
        Member user = new Member(name, email, password);
        try {
            dao.create(user);
        } catch (Exception e) {
            return ErrorResponse.fromException(e).build();
        }
        URI location = UriBuilder.fromPath(user.getName().toLowerCase()).build();
        return Response.created(location).build();
    }

    @GET
    @Path("/{name}")
    public Response show(@PathParam("name") String name) {
        Member user = dao.findByEmail(name);
        if (user == null) {
            return new ErrorResponse(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(user).build();
    }

}
