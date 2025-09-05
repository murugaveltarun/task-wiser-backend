package com.tarun.TaskManagement.service;

import com.tarun.TaskManagement.model.UserPrincipal;
import com.tarun.TaskManagement.model.Users;
import com.tarun.TaskManagement.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    public UsersRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = repo.findByUsername(username);

        if(user == null){
            System.out.println("User not found");
            throw new UsernameNotFoundException("User not found");
        }

        if(!user.getUsername().equals(username)){
            throw new UsernameNotFoundException("User not found");

        }
        System.out.println("User found from user details");
        return new UserPrincipal(user) ;
    }
}
