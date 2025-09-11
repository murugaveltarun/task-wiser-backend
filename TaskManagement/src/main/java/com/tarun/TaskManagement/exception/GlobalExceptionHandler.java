package com.tarun.TaskManagement.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponseModel<Void>> handleExpiredJwt(ExpiredJwtException e){
        ApiResponseModel<Void> response = new ApiResponseModel<>(false,"JWT Token is expired. Please log in again.", HttpStatus.UNAUTHORIZED.value(), null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<ApiResponseModel<Void>> handleIllegalAccessException(IllegalAccessException e){
        ApiResponseModel<Void> response = new ApiResponseModel<>(false,e.getMessage(),HttpStatus.UNAUTHORIZED.value(), null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(MissingFieldException.class)
    public ResponseEntity<ApiResponseModel<Void>> handleMissingFieldException(MissingFieldException e){
        ApiResponseModel<Void> response = new ApiResponseModel<>(false,e.getMessage(),HttpStatus.BAD_REQUEST.value(), null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponseModel<Void>> handleEntityNotFoundException(EntityNotFoundException e){
        ApiResponseModel<Void> response = new ApiResponseModel<>(false,e.getMessage(),HttpStatus.NOT_FOUND.value(), null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseModel<Void>> handleBadCredentialsException(BadCredentialsException e){
        ApiResponseModel<Void> response = new ApiResponseModel<>(false,e.getMessage(),HttpStatus.UNAUTHORIZED.value(), null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
