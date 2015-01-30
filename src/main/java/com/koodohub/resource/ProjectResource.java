package com.koodohub.resource;

import com.google.common.base.Optional;
import com.koodohub.domain.*;
import com.koodohub.service.ProjectService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

    private final ProjectService projectService;

    private final static Logger log = LoggerFactory.getLogger(ProjectResource.class);
    private SecureRandom secureRandom = new SecureRandom();

    public ProjectResource(ProjectService projectService) {
        this.projectService = projectService;
    }

    @POST
    @UnitOfWork
    public Response create(@Auth User user, @Valid Project projectEntry) {
        log.info("{} create project {} {} {}", user.getUsername(), projectEntry.getTitle(),
                projectEntry.getMedialink(), projectEntry.getDescription());
        Project project = projectService.createProject(projectEntry.getTitle(),
                projectEntry.getDescription(), user, projectEntry.getMedialink());
        return new SuccessResponse<Project>(Response.Status.CREATED,
                "Project "+projectEntry.getTitle()+" is posted.", project).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Project show(@Auth User user, @PathParam("id") int id) {
        log.debug("querying project information:{}", id);
        final Optional<Project> projectInfo = projectService.getProjectById(id);
        if (!projectInfo.isPresent()) {
            throw new WebApplicationException(404);
        }
        return projectInfo.get();
    }

    @GET
    @Path("/{id}/getComments")
    @UnitOfWork
    public List<Comment> getComments(@Auth User user,
                            @PathParam("id") int project_id) {
        log.debug("get comments on project {}", project_id);
        final Optional<Project> projectInfo = projectService.getProjectById(project_id);
        if (projectInfo.isPresent()) {
            return projectInfo.get().getComments();
        }
        return Collections.emptyList();
    }

    @POST
    @Path("/{id}/comment")
    @UnitOfWork
    public Response comment(@Auth User user,
                            @PathParam("id") int project_id,
                            @DefaultValue("-1") @QueryParam("replyto_id") int replyto_id,
                            @QueryParam("comment") String commentText) {
        log.debug("{} comment on project {}", user.getUsername(), project_id);
        final Optional<Project> projectInfo = projectService.getProjectById(project_id);
        if (projectInfo.isPresent()) {
            Comment comment = projectService.createComment(user, projectInfo.get(), replyto_id, commentText);
            return new SuccessResponse<Comment>(Response.Status.OK, "Comment is posted",
                    comment).build();
        }
        return new ErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                "Unable to post comment.").build();
    }

    @GET
    @Path("/{id}/getFavorites")
    @UnitOfWork
    public List<Favorite> getFavorites(@Auth User user,
                                 @PathParam("id") int project_id) {
        log.debug("get favorites on project {}", project_id);
        final Optional<Project> projectInfo = projectService.getProjectById(project_id);
        if (projectInfo.isPresent()) {
            Hibernate.initialize(projectInfo.get().getFavorites());
            List<Favorite> favorites = projectInfo.get().getFavorites();
            log.debug("favorites: {}", favorites.size());
            return favorites;
        }
        return Collections.emptyList();
    }

    @GET
    @Path("/{id}/favorite")
    @UnitOfWork
    public Response favorite(@Auth User user,
                            @PathParam("id") int project_id) {
        log.debug("{} favorite on project {}", user.getUsername(), project_id);
        final Optional<Project> projectInfo = projectService.getProjectById(project_id);
        if (projectInfo.isPresent()) {
            Favorite favorite = projectService.createFavorite(user, projectInfo.get());
            return new SuccessResponse<Favorite>(Response.Status.OK,
                    "You have marked \"{}\" as favorite."+projectInfo.get().getTitle(),
                    favorite).build();
        }
        return new ErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                "Unable to favorite the project.").build();
    }

    @POST
    @Path("/uploadFile")
    @UnitOfWork
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@Auth User user,
                                 @FormDataParam("file") final InputStream uploadStream,
                                 @FormDataParam("file") FormDataContentDisposition fileDetail) {
        String newFileName = "P"+secureRandom.nextInt()+"_"+fileDetail.getFileName();
        java.nio.file.Path outputPath = FileSystems.getDefault().getPath("media/projects/", newFileName);
        try {
            Files.copy(uploadStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
            String fileLink = "projects/"+newFileName;
            return new SuccessResponse<String>(Response.Status.OK,
                    "", fileLink).build();
        } catch (IOException e) {
            log.error("Failed to save file {} for user {}",
                    fileDetail.getFileName(),
                    user.getUsername(),
                    e);
            return new ErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    "Unable to upload file "+fileDetail.getFileName()).build();
        }
    }
}

