package com.koodohub.domain;

import javax.ws.rs.core.Response;

public class SuccessResponse<T> {
    private final int status;
    private final String message;
    private final T data;

    public SuccessResponse(Response.Status status, String message, T data) {
        this.status = status.getStatusCode();
        this.message = message;
        this.data = data;
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

    public T getData() { return data; }
}
