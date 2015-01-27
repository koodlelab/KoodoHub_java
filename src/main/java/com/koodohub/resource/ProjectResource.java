package com.koodohub.resource;

import com.google.common.base.Optional;
import com.koodohub.domain.ErrorResponse;
import com.koodohub.domain.Project;
import com.koodohub.domain.SuccessResponse;
import com.koodohub.domain.User;
import com.koodohub.service.ProjectService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    @Path("/getUserProjects")
    @UnitOfWork
    public Set<Project> getUserProjects(@Auth User user, @QueryParam("username") String username) {
//        List<Project> projects =  projectService.getProjectsByUsername(username);
        Set<Project> projects = user.getProjects();
        log.debug("querying projects by username:{} size:{}", username, projects.size());
        return projects;
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

