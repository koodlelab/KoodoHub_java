package com.koodohub.dao;

import com.google.common.base.Optional;
import com.koodohub.domain.Comment;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

public class CommentDAO extends AbstractDAO<Comment> {

    public CommentDAO(SessionFactory factory) {
        super(factory);
    }

    public Comment save(Comment comment) {
        return persist(comment);
    }

    public Optional<Comment> findById(int id) {
        return Optional.fromNullable(uniqueResult(namedQuery("Comment.findById")
                .setInteger("id", id)));
    }

}