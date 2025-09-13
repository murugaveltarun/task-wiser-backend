package com.tarun.TaskManagement.model;

public class ResetPassword {
    private String password;
    private String token;

    public ResetPassword(String password, String token) {
        this.password = password;
        this.token = token;
    }

    @Override
    public String toString() {
        return "ResetPassword{" +
                "password='" + password + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
