package com.koodohub.jdbc;

import com.google.common.base.Optional;
import com.koodohub.domain.Comment;
import com.koodohub.domain.Favorite;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

public class FavoriteDAO extends AbstractDAO<Favorite> {

    public FavoriteDAO(SessionFactory factory) {
        super(factory);
    }

    public Favorite save(Favorite favorite) {
        return persist(favorite);
    }

    public Optional<Favorite> findById(int id) {
        return Optional.fromNullable(uniqueResult(namedQuery("Favorite.findById")
                .setInteger("id", id)));
    }

}