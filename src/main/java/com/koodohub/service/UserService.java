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

    public List<String> getFollowings(final String username) {
        List<Relationship> relationships = this.relationshipDAO.findFollowingByUsername(username);
        List<String> users = new ArrayList<>(relationships.size());
        for (Relationship relationship : relationships) {
            users.add(relationship.getFollowed());
        }
        return users;
    }

    public List<String> getFollowedBy(final String username) {
        List<Relationship> relationships = this.relationshipDAO.findFollowedByUsername(username);
        List<String> users = new ArrayList<>(relationships.size());
        for (Relationship relationship : relationships) {
            users.add(relationship.getFollower());
        }
        return users;
    }
}
