package com.koodohub.security;

import com.google.common.base.Optional;
import com.koodohub.domain.User;
import com.koodohub.jdbc.UserDAO;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KoodoHubAuthenticator implements Authenticator<BasicCredentials, User> {

    private final Logger log = LoggerFactory.getLogger(KoodoHubAuthenticator.class);
    private final UserDAO userDAO;

    public KoodoHubAuthenticator(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        log.debug("authenticate user {}", credentials.getUsername());
        Optional<User> userDetails = null;
        userDetails = userDAO.findByLogin(credentials.getUsername());
        if (!userDetails.isPresent()
                || !userDetails.get().isCorrectPassword(credentials.getPassword())
                || !userDetails.get().isActivated()) {
            throw new InvalidCredentialException("Access denied.");
        }
        return userDetails;
    }

    public Optional<User> authenticate(String userName, String authToken) throws AuthenticationException {
        Optional<User> userDetails = this.userDAO.findByUsername(userName);
        if (TokenUtils.validateToken(authToken, userDetails.get())) {
            return userDetails;
        }
        return userDetails.absent();
    }
}
