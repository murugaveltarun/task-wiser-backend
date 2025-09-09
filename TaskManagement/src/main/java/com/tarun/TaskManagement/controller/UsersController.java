package com.tarun.TaskManagement.controller;

import com.tarun.TaskManagement.exception.ApiResponseModel;
import com.tarun.TaskManagement.model.Users;
import com.tarun.TaskManagement.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;


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
    public String login(@RequestBody Users user){
        System.out.println("Login controller : " + user);
        return service.verify(user);
    }


}
