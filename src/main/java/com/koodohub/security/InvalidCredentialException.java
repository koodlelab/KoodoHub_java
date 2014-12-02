package com.koodohub.security;

import io.dropwizard.auth.AuthenticationException;

public class InvalidCredentialException extends AuthenticationException  {

    public InvalidCredentialException(String message) {
        super(message);
    }
}
