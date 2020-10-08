package com.example.shoppinglist.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private List<Product> userShoppingList = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
