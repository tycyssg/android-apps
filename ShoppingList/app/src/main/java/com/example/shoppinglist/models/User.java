package com.example.shoppinglist.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
