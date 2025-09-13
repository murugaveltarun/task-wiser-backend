package com.tarun.TaskManagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "forgot_password")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ForgotPassword {

    @Column(name = "user_id",nullable = false)
    @Id
    private int userId;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "expiry", nullable = false)
    private LocalDateTime expiry;

}
