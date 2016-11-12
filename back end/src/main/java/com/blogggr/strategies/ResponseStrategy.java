package com.blogggr.strategies;

import org.springframework.http.ResponseEntity;

/**
 * Created by Daniel Sunnen on 01.11.16.
 */
public interface ResponseStrategy {
    ResponseEntity notAuthorizedResponse();

    ResponseEntity invalidInputResponse(String errorMessage);

    ResponseEntity exceptionResponse(String exceptionMessage);

    ResponseEntity successResponse(Object data);

    ResponseEntity notFound(String errorMessage);
}
