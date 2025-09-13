package com.tarun.TaskManagement.repository;

import com.tarun.TaskManagement.model.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPasswordRepo extends JpaRepository<ForgotPassword,Integer> {

    //find by token
    ForgotPassword findByToken(String token);
}
