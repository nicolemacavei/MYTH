package com.example.myth;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;

public class Connection implements Serializable {

    public String userId;

    public Connection(String userId) {
        this.userId = userId;
    }
}
