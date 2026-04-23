package com.smartcampus.exceptions.mappers;

import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.smartcampus.models.ErrorMessage;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        // HTTP 422 Unprocessable Entity
        ErrorMessage errorMessage = new ErrorMessage(
                422,
                "Unprocessable Entity",
                exception.getMessage()
        );
        return Response.status(422)
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
