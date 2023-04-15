package com.example.myth;

public class User {
    public String email, password, lastName, firstName, image;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getImage() {
        return image;
    }

    public User(String email, String password, String lastName, String firstName, String image) {
        this.email = email;
        this.password = password;
        this.lastName = lastName;
        this.firstName = firstName;
        this.image = image;
    }

    public boolean verifyPassword(String password){
        if(password.length() < 8 || (!password.matches(".*[\\d].*") && !password.matches(".*[a-z].*"))){
            return false;
        }
        return true;
    }
}
