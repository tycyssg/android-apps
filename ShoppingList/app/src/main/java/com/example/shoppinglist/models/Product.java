package com.example.shoppinglist.models;

import lombok.Data;

@Data
public class Product {
    private String productName;
    private Double productPrice;
    private int image;

    public Product(String productName, Double productPrice, int image) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.image = image;
    }
}
