package com.lescale.restaurant.ModelClasses;


public class CartModel {
    public String getItem_id() {
        return item_id;
    }

    public String getId() {
        return id;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    String item_id;
    String id;
    String quantity;
    String price;

    public String getTitle() {
        return title;
    }

    String title;



    public CartModel(String item_id, String id, String quantity, String price, String title) {
        this.item_id = item_id;
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.title = title;
    }
    public CartModel() {
    }
}
