package com.koodohub.service;

import com.google.common.base.Optional;
import com.koodohub.domain.Comment;
import com.koodohub.domain.Favorite;
import com.koodohub.domain.Project;
import com.koodohub.domain.User;
import com.koodohub.jdbc.CommentDAO;
import com.koodohub.jdbc.FavoriteDAO;
import com.koodohub.jdbc.ProjectDAO;

import java.util.List;

public class ProjectService {

    private final ProjectDAO projectDAO;
    private final CommentDAO commentDAO;
    private final FavoriteDAO favoriteDAO;

    public ProjectService(final ProjectDAO projectDAO, CommentDAO commentDAO, FavoriteDAO favoriteDAO) {
        this.projectDAO = projectDAO;
        this.commentDAO = commentDAO;
        this.favoriteDAO = favoriteDAO;
    }

    public Project createProject(final String title, final String description,
                                 final User owner, final String mediaLink) {
        Project project = new Project();
        project.init(title, description, owner, mediaLink);
        projectDAO.save(project);
        return project;
    }

    public Optional<Project> getProjectById(final int id) {
        return projectDAO.findById(id);
    }

    public Comment createComment(final User user, final Project project,
                                 final int replyTo, final String commentText) {
        Comment comment = new Comment();
        Comment replyToComment = null;
        if (replyTo > 0) {
            replyToComment = getCommentById(replyTo).get();
        }
        comment.init(user, project, commentText, replyToComment);
        commentDAO.save(comment);
        return comment;
    }

    public Optional<Comment> getCommentById(final int id) {
        return commentDAO.findById(id);
    }

    public Favorite createFavorite(final User user, final Project project) {
        Favorite favorite = new Favorite();
        favorite.init(user, project);
        favoriteDAO.save(favorite);
        return favorite;
    }

}
