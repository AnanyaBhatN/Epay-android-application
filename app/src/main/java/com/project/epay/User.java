package com.project.epay;


public class User {
    public String mobile, email, password;

    public User() {
        // Default constructor required by Firebase
    }

    public User(String mobile, String email, String password) {
        this.mobile = mobile;
        this.email = email;
        this.password = password;
    }
}
