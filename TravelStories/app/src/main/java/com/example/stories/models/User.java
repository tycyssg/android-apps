package com.example.stories.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String fullName;
    private String email;
    private String phone;
    private String photoUrl;
    private String userId;
    private Date createdDate;

    public User(String fullName, String phone, String photoUrl) {
        this.fullName = fullName;
        this.phone = phone;
        this.photoUrl = photoUrl;
    }

    public User(String fullName, String email, String phone, String photoUrl, String userId) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.userId = userId;
        this.createdDate = new Date();
    }
}
