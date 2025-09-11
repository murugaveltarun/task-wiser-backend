package com.tarun.TaskManagement.service;

import com.tarun.TaskManagement.exception.ApiResponseModel;
import com.tarun.TaskManagement.model.Users;
import com.tarun.TaskManagement.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    @Autowired
    private JwtService service;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UsersRepo repo;




    public ApiResponseModel<Void> register(Users user){
        if(user.getUsername() == null || user.getPassword() == null || user.getRole() == null || user.getName() == null){
            return new ApiResponseModel<>(false,"Missing field",HttpStatus.PARTIAL_CONTENT.value(),null);
        }
        if(repo.findByUsername(user.getUsername()) != null){
            return new ApiResponseModel<>(false,"User already exists",HttpStatus.CONFLICT.value(),null);
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        System.out.println("User Created successfully with username : " + user.getUsername()  + " and password : " + user.getPassword());
        return new ApiResponseModel<>(true,"User Created Successfully", HttpStatus.CREATED.value(),null);
    }

    public String verify(Users user){

        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        if(authentication.isAuthenticated()){
            Users dbUser = repo.findByUsername(user.getUsername());
            String token = service.generateToken(dbUser);
            System.out.println("Token generated for userId : " + dbUser.getUsername() + " token : " + token);
            return token;
        }
        throw new BadCredentialsException("Invalid username or password.");
    }

}
