package com.microservices.organization.exception;

import com.microservices.organization.payload.APIErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIErrorDetails> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                           WebRequest webRequest){
        APIErrorDetails apiErrorDetails = new APIErrorDetails(new Date(),
                exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<APIErrorDetails>(apiErrorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<APIErrorDetails> handleNumberFormatException(OrganizationNotFoundException exception,
                                                                       WebRequest webRequest) {
        APIErrorDetails apiErrorDetails = new APIErrorDetails(new Date(),
                exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(apiErrorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIErrorDetails> handleGlobalException(Exception exception,
                                                                 WebRequest webRequest){
        APIErrorDetails apiErrorDetails = new APIErrorDetails(new Date(),
                exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<APIErrorDetails>(apiErrorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}