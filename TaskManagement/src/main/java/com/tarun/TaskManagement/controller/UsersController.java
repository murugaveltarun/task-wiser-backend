package com.tarun.TaskManagement.controller;

import com.tarun.TaskManagement.exception.ApiResponseModel;
import com.tarun.TaskManagement.model.ResetPassword;
import com.tarun.TaskManagement.model.Users;
import com.tarun.TaskManagement.service.Oauth2Service;
import com.tarun.TaskManagement.service.UsersService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;


@RestController
@CrossOrigin
public class UsersController {

    @Autowired
    private UsersService service;

    @Autowired
    private Oauth2Service oauthService;


    @GetMapping("/")
    public String hello(){
        return "Hello";
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
    public ResponseEntity<ApiResponseModel<String>>  login(@RequestBody Users user, HttpServletResponse servletResponse) throws IllegalAccessException {
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


//      localhost:8080/oauth2/authorization/google  -> use this in frontend. this is used to authenticate.
//      localhost:8080/login/oauth2/code/google     -> once authenticated, this will be used to login. once login, redirected to oauth2/success.

    @GetMapping("/oauth2/success")
    public RedirectView success(OAuth2AuthenticationToken token, HttpServletResponse servletResponse) throws IllegalAccessException {
        return oauthService.auth(token,servletResponse);

    }
    @GetMapping("/oauth2/failed")
    public ResponseEntity<ApiResponseModel<?>> failed(){
        ApiResponseModel<Void> response = new ApiResponseModel<>(false,"Oauth2 verification failed.",HttpStatus.UNAUTHORIZED.value(), null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}



































