package com.koodohub.resource;

import com.koodohub.domain.ErrorResponse;
import com.koodohub.domain.Project;
import com.koodohub.domain.SuccessResponse;
import com.koodohub.domain.User;
import com.koodohub.service.ProjectService;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

    private final ProjectService projectService;
    private final static Logger log = LoggerFactory.getLogger(Project.class);

    public ProjectResource(ProjectService projectService) {
        this.projectService = projectService;
    }

    @POST
    @UnitOfWork
    public Response create(@Auth User user, @Valid Project projectEntry) {
        log.info("{} create project {}", user.getUserName(), projectEntry.getTitle());
        return new SuccessResponse(Response.Status.CREATED,
                "Project "+projectEntry.getTitle()+" is posted.").build();
    }
}

