package com.koodohub.core;

import javax.ws.rs.core.Response;
import java.sql.SQLException;

public class ErrorResponse {
    private final int status;
    private final String[] errors;

    public ErrorResponse(Response.Status status, String... errors) {
        this.status = status.getStatusCode();
        this.errors = errors;
    }
//
//    public ErrorResponse(Response.Status status, String error) {
//        this.status = status.getStatusCode();
//        this.errors = new String[1];
//        this.errors[0] = error;
//    }

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