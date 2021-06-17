package com.lescale.restaurant.ModelClasses;

public class UserModel {

    String name,phone,email,uid;

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public UserModel() {
    }

    public UserModel(String name, String phone, String email, String uid) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.uid = uid;
    }
}
