package com.koodohub.security;

import com.google.common.base.Optional;
import com.koodohub.domain.User;
import com.koodohub.jdbc.UserDAO;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

public class KoodoHubAuthenticator implements Authenticator<BasicCredentials, User> {

    private final UserDAO userDAO;

    public KoodoHubAuthenticator(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        Optional<User> userDetails = null;
        System.out.println("authenticated call.");
        userDetails = userDAO.findByLogin(credentials.getUsername());
        if (!userDetails.isPresent() || !userDetails.get().isCorrectPassword(credentials.getPassword())) {
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
