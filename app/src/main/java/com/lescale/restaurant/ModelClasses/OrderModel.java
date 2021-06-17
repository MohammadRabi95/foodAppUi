package com.lescale.restaurant.ModelClasses;

import org.jetbrains.annotations.Contract;

public class OrderModel {

    String date;
    String time;
    String items;
    String price;
    String phone;
    String name;
    String user_id;
    String pID;
    String type;
    String latitude;
    String longitude;
    String orderid;
    String address;
    String message;
    String deliveryFee;

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public String getMessage() {
        return message;
    }

    public String getAddress() {
        return address;
    }

    public String getOrderid() {
        return orderid;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getType() {
        return type;
    }

    public String getpID() {
        return pID;
    }

    public OrderModel() {
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getItems() {
        return items;
    }

    public String getPrice() {
        return price;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getUser_id() {
        return user_id;
    }

    public OrderModel(String date, String time, String items, String price,
                      String phone, String name, String user_id, String orderid,
                      String pID, String type, String latitude, String longitude,
                      String address, String message, String deliveryFee) {
        this.date = date;
        this.time = time;
        this.items = items;
        this.price = price;
        this.phone = phone;
        this.name = name;
        this.user_id = user_id;
        this.pID = pID;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.orderid = orderid;
        this.address = address;
        this.message = message;
        this.deliveryFee = deliveryFee;

    }
}
