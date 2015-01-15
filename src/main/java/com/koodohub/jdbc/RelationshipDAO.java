package com.koodohub.jdbc;

import com.google.common.base.Optional;
import com.koodohub.domain.Relationship;
import com.koodohub.domain.User;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

public class RelationshipDAO extends AbstractDAO<Relationship> {

    public RelationshipDAO(SessionFactory factory) {
        super(factory);
    }

    public Relationship save(Relationship relationship) {
        return persist(relationship);
    }

    public List<Relationship> findFollowingByUsername(String username) {
        return list(namedQuery("Relationship.findFollowingByUsername")
                .setString("username", username));
    }

    public List<Relationship> findFollowedByUsername(String username) {
        return list(namedQuery("Relationship.findFollowedByUsername")
                .setString("username", username));
    }
}