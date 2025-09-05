package com.tarun.TaskManagement.service;

import com.tarun.TaskManagement.model.Users;
import com.tarun.TaskManagement.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
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




    public String register(Users user){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        System.out.println(user.getPassword());
        user.setPassword(encoder.encode(user.getPassword()));
        System.out.println(user.getPassword());
        repo.save(user);
        return "Registered";
    }

    public String verify(Users user){

        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        if(authentication.isAuthenticated()){
            Users dbUser = repo.findByUsername(user.getUsername());
            return service.generateToken(dbUser);
        }
        return "Failed";

    }

}
