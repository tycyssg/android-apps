package com.example.stories.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

    private String fullName;
    private String email;
    private String phone;
    private String photoUrl;

    public User(String fullName, String phone, String photoUrl) {
        this.fullName = fullName;
        this.phone = phone;
        this.photoUrl = photoUrl;
    }
}
