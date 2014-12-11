package com.koodohub.service;

import com.google.common.base.Optional;
import com.koodohub.domain.User;
import com.koodohub.jdbc.UserDAO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserService {

    private final UserDAO userDAO;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);


    public UserService(final UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User createUser(final String username, final String password,
                           final String fullname, final String email) {
        User newUser = new User();
        newUser.init(fullname, email, password, username, "MEMBER");
        userDAO.save(newUser);
        return newUser;
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
}
