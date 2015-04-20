package com.koodohub.dao;

import com.koodohub.domain.Relationship;
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

    public void delete(Relationship relationship) {
        currentSession().delete(relationship);
    }

    public List<Relationship> findFollowers(final String username) {
        return list(namedQuery("Relationship.findFollowersByUser").setString("username", username));
    }

    public List<Relationship> findFollowings(final String username) {
        return list(namedQuery("Relationship.findFollowingsByUser").setString("username", username));
    }

}