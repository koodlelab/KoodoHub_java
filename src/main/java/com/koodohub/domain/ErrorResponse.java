package com.koodohub.domain;

import javax.ws.rs.core.Response;

public class ErrorResponse {
    private final int status;
    private final String[] errors;

    public ErrorResponse(Response.Status status, String... errors) {
        this.status = status.getStatusCode();
        this.errors = errors;
    }

    public Response build() {
        return Response
                .status(getStatus())
                .entity(this)
                .build();
    }

    public int getStatus() {
        return status;
    }

    public String[] getErrors() {
        return errors;
    }
}