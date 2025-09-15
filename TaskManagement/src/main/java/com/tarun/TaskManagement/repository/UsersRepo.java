package com.tarun.TaskManagement.repository;

import com.tarun.TaskManagement.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepo extends JpaRepository<Users,Integer> {


    //search by username
    Users findByUsername(String username);

    //search by email
    Users findByEmail(String email);

    //search by role
    List<Users> findByRole(String userRole);

    //find by authProvider and providerId
    Users findByAuthProviderAndProviderId(String authProvider, String providerId);

    //find by authProvider and email
    Users findByAuthProviderAndEmail(String authProvider, String email);
}
