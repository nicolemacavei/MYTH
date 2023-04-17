package com.example.myth;

import java.io.Serializable;

public class User implements Serializable {
    public String email, password, fullName, image, token;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getImage() {
        return image;
    }

    public String getFullName(){ return fullName; }

    public User(String email, String fullName, String image, String token) {
        this.email = email;
        this.fullName = fullName;
        this.image = image;
        this.token = token;
    }

    public boolean verifyPassword(String password){
        if(password.length() < 8 || (!password.matches(".*[\\d].*") && !password.matches(".*[a-z].*"))){
            return false;
        }
        return true;
    }
}
