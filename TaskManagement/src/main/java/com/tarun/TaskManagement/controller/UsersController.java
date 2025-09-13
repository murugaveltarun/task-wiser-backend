package com.tarun.TaskManagement.controller;

import com.tarun.TaskManagement.exception.ApiResponseModel;
import com.tarun.TaskManagement.model.ResetPassword;
import com.tarun.TaskManagement.model.Users;
import com.tarun.TaskManagement.service.UsersService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;import org.springframework.web.context.request.RequestContextHolder;


@RestController
@CrossOrigin
public class UsersController {

    private final UsersService service;
    @Autowired
    public UsersController(UsersService service) {
        this.service = service;
    }

    //just to test and to check whether session is stateless
    //SecurityFilerChain in SecurityConfig was configured to stay stateless
    //Because we have jwt authentication which will expire. expiration time is mentioned in jwtService
    @GetMapping("/")
    public String greet(){
        return "Hello " + RequestContextHolder.currentRequestAttributes().getSessionId();
    }


    //to register a new user
    @PostMapping("/register")
    public ResponseEntity<ApiResponseModel<Void>> register(@RequestBody Users user){
//        System.out.println("User tried to register : " + user);
        ApiResponseModel<Void> response = service.register(user);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //to login a registered user
    @PostMapping("/login")
    public ResponseEntity<ApiResponseModel<String>>  login(@RequestBody Users user, HttpServletResponse servletResponse){
        System.out.println("Login controller : " + user);
        ApiResponseModel<String> response = new ApiResponseModel<>(true,"User Verified.", HttpStatus.OK.value(),service.verify(user,servletResponse));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseModel<String>> getAccessToken(@CookieValue(value = "refreshToken",required = false) String refreshToken) throws IllegalAccessException {
        ApiResponseModel<String> response = new ApiResponseModel<>(true,"User verified and access token is generated",HttpStatus.OK.value(), service.generateAccessToken(refreshToken));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponseModel<Void>> forgotPassword(@RequestBody Users user){
        service.forgotPassword(user);
        ApiResponseModel<Void> response = new ApiResponseModel<>(true,"Reset password link will be sent if the email exists.",HttpStatus.OK.value(), null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseModel<Void>> resetPassword(@RequestBody ResetPassword resetPassword) throws IllegalAccessException {
        service.resetPassword(resetPassword);
        ApiResponseModel<Void> response = new ApiResponseModel<>(true,"Password reset success",HttpStatus.OK.value(), null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


}



































