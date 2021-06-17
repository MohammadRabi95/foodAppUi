package com.lescale.restaurant.ModelClasses;

public class MenuModel {

    String imageUrl,title,description,price,rating,id;

    public MenuModel() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getRating() {
        return rating;
    }

    public String getId() {
        return id;
    }

    public MenuModel(String imageUrl, String title, String description, String price, String rating, String id) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.id = id;
    }
}
