package com.koodohub.domain;

import javax.ws.rs.core.Response;

public class SuccessResponse {
    private final int status;
    private final String message;

    public SuccessResponse(Response.Status status, String message) {
        this.status = status.getStatusCode();
        this.message = message;
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

    public String getMessage() {
        return message;
    }
}
