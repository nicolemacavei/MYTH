package com.example.myth;

import java.io.Serializable;

public class User implements Serializable {
    public String userId, email, fullName, image, token;

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public String getFullName(){ return fullName; }

    public User(String userId, String email, String fullName, String image, String token) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.image = image;
        this.token = token;
    }
}
