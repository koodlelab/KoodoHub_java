package com.koodohub.service;

import com.google.common.base.Optional;
import com.koodohub.domain.Relationship;
import com.koodohub.domain.User;
import com.koodohub.jdbc.RelationshipDAO;
import com.koodohub.jdbc.UserDAO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final UserDAO userDAO;
    private final RelationshipDAO relationshipDAO;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);

    public UserService(final UserDAO userDAO, final RelationshipDAO relationshipDAO) {
        this.userDAO = userDAO;
        this.relationshipDAO = relationshipDAO;
    }

    public User createUser(final String username, final String password,
                           final String fullname, final String email) {
        User newUser = new User();
        newUser.init(fullname, email, password, username, "MEMBER");
        userDAO.save(newUser);
        return newUser;
    }

    public List<User> getAllUsers() {
        return userDAO.findAllUsers();
    }

    public Optional<User> getUserByEmail(final String email) {
        return userDAO.findByEmail(email);
    }

    public Optional<User> getUserByUsername(final String username) {
        return userDAO.findByUsername(username);
    }

    /**
     * @param login  could be email or username
     * @return
     */
    public Optional<User> getUserByLogin(final String login) {
        return userDAO.findByLogin(login);
    }

    public Optional<User> activateUser(final String email, final String token) {
        Optional<User> userEntry = userDAO.findByEmail(email);
        if (userEntry.isPresent()) {
            User user = userEntry.get();
            if (user.getActivationKey().equals(token)) {
                user.setActivated(true);
                return Optional.of(user);
            }
        }
        return Optional.absent();
    }

    public void followUser(final User follower, final User followed) {
        Relationship relationship = new Relationship();
        relationship.init(follower, followed);
        relationshipDAO.save(relationship);
    }

    public void unfollowUser(final User follower, final User followed) {
        Relationship relationship = new Relationship();
        relationship.init(follower, followed);
        relationshipDAO.delete(relationship);
    }

    public List<User> getFollowersByUser(final String username) {
        List<Relationship> relationships = relationshipDAO.findFollowers(username);

        List<User> followers = new ArrayList<>(relationships.size());
        for (Relationship relationship: relationships) {
            followers.add(relationship.getFollowing());
        }
        return followers;
    }

    public List<User> getFollowingsByUser(final String username) {
        List<Relationship> relationships = relationshipDAO.findFollowings(username);

        List<User> followings = new ArrayList<>(relationships.size());
        for (Relationship relationship: relationships) {
            followings.add(relationship.getFollowing());
        }
        return followings;
    }
}
