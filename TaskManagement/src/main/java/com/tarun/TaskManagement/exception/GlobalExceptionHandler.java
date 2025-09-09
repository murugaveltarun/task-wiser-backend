package com.tarun.TaskManagement.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponseModel<Void>> handleExpiredJwt(ExpiredJwtException e){
        ApiResponseModel<Void> response = new ApiResponseModel<>(false,"JWT Token is expired. Please log in again.", HttpStatus.UNAUTHORIZED.value(), null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
