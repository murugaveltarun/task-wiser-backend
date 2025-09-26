package com.tarun.TaskManagement.repository;

import com.tarun.TaskManagement.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepo extends JpaRepository<Users,Integer> {


    //search by username
    Users findByUsername(String username);

    //search by email
    Users findByEmail(String email);

    //search by role
    Page<Users> findByRole(String userRole, Pageable pageable);

    //find by authProvider and providerId
    Users findByAuthProviderAndProviderId(String authProvider, String providerId);

    //find by authProvider and email
    Users findByAuthProviderAndEmail(String authProvider, String email);

    //search user
    @Query("SELECT u FROM Users u WHERE " +
            "(:username IS NULL OR u.username LIKE CONCAT('%',:username,'%')) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%',:email,'%'))) AND " +
            "(:authProvider IS NULL OR LOWER(u.authProvider) LIKE LOWER(CONCAT('%',:authProvider,'%'))) AND " +
            "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%',:name,'%'))) AND " +
            "(:id IS NULL OR u.id = :id) AND " +
            "(:active IS NULL OR u.isActive = :active) ")
    Page<Users> searchByUsers(String email, String username, String name, Boolean active,String authProvider, Integer id, Pageable pageable);
}
