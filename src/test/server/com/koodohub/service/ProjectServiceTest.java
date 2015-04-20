package com.koodohub.service;

import com.koodohub.dao.CommentDAO;
import com.koodohub.dao.FavoriteDAO;
import com.koodohub.dao.ProjectDAO;
import com.koodohub.domain.Project;
import com.koodohub.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;

public class ProjectServiceTest {

    private ProjectService projectService;

    @Mock private ProjectDAO projectDAO;
    @Mock private CommentDAO commentDAO;
    @Mock private FavoriteDAO favoriteDAO;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        projectService = new ProjectService(projectDAO, commentDAO, favoriteDAO);
    }

    @Test
    public void testCreateProject() {
        final String title = "TEST";
        final String description = "DESC";
        final User user = new User();
        user.setUsername("OWNER");
        final String mediaLink = "testFile";
        Project project = projectService.createProject(title, description, user, mediaLink);
        verify(projectDAO, times(1)).save(project);
    }

}
