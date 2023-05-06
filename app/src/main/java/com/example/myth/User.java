package com.example.myth;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable, Parcelable {
    public String userId, email, fullName, image, token;

    protected User(Parcel in) {
        userId = in.readString();
        email = in.readString();
        fullName = in.readString();
        image = in.readString();
        token = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(email);
        dest.writeString(fullName);
        dest.writeString(image);
        dest.writeString(token);
    }
}
