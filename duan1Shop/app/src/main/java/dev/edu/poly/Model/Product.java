package dev.edu.poly.Model;

import java.io.Serializable;

public class Product implements Serializable {
    private String idKey;
    private String name;
    private String price;
    private String image;
    private String description;
    private String brand;
    private int quantity;

    public Product() {
    }

    public Product(String idKey, String name, String price, String image, String description, String brand, int quantity) {
        this.idKey = idKey;
        this.name = name;
        this.price = price;
        this.image = image;
        this.description = description;
        this.brand = brand;
        this.quantity = quantity;
    }

    public String getIdKey() {
        return idKey;
    }

    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
