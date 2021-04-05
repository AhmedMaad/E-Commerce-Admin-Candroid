package com.maad.buyfromcandroidadmin;

public class ProductModel {

    private String title;
    private String description;
    private double price;
    private int quantity;
    private String category;
    private String image;
    private String id;

    //Required Empty Constructor for reading
    public ProductModel(){}

    public ProductModel(String title, String description, double price
            , int quantity, String category, String image) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }
}
