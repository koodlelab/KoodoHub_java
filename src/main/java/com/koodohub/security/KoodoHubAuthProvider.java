package com.koodohub.security;

import com.google.common.base.Optional;
import com.koodohub.domain.User;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;


public class KoodoHubAuthProvider implements InjectableProvider <Auth, Parameter> {

    public final static String CUSTOM_HEADER = "X-Auth-Token";

    private final KoodoHubAuthenticator authenticator;

    public KoodoHubAuthProvider(KoodoHubAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    private static class KoodoHubSecurityInjectable extends AbstractHttpContextInjectable<User> {

        private final KoodoHubAuthenticator authenticator;
        private final boolean required;

        private KoodoHubSecurityInjectable(KoodoHubAuthenticator authenticator, boolean required) {
            this.authenticator = authenticator;
            this.required = required;
        }

        @Override
        public User getValue(HttpContext c) {
            // This is where the credentials are extracted from the request
            final String authToken = c.getRequest().getHeaderValue(CUSTOM_HEADER);
            String userName = TokenUtils.getUserNameFromToken(authToken);
            try {
                if (authToken != null) {
                    final Optional<User> result = authenticator.authenticate(
                            userName, authToken);
                    if (result.isPresent()) {
                        return result.get();
                    }
                }
            } catch (AuthenticationException e) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }

            if (required) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }

            return null;
        }

    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, Auth auth, Parameter parameter) {
        return new KoodoHubSecurityInjectable(authenticator, auth.required());
    }
}
