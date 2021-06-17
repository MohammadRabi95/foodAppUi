package com.lescale.restaurant.Classes;


import android.app.Activity;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class MyFirebaseReferences {

    @NotNull
    public static DatabaseReference userInfoReference() {
        return FirebaseDatabase.getInstance().getReference(Utils.USERS_DB)
                .child(AppHelper.getUID()).child(Utils.USER_INFO);
    }

    @NotNull
    public static DatabaseReference menuReference(Activity activity) {
        return FirebaseDatabase.getInstance().getReference(Utils.MENUS_DB)
                .child(AppHelper.getLanguage(activity))
                .child(AppHelper.getCategory(activity));
    }

    @NotNull
    public static DatabaseReference cartReference() {
        return FirebaseDatabase.getInstance().getReference(Utils.USERS_DB)
                .child(AppHelper.getUID()).child(Utils.CART);
    }

    @NotNull
    public static DatabaseReference userOrderReference() {
        return FirebaseDatabase.getInstance().getReference(Utils.USERS_DB)
                .child(AppHelper.getUID()).child(Utils.ORDERS_DB);
    }

    @NotNull
    public static DatabaseReference restaurantOrderReference() {
        return FirebaseDatabase.getInstance().getReference(Utils.ORDERS_DB);
    }

    @NotNull
    public static DatabaseReference promoDialogReference() {
        return FirebaseDatabase.getInstance().getReference(Utils.DIALOG).child(Utils.IMG);
    }

    @NotNull
    public static DatabaseReference userPhoneNumber() {
        return FirebaseDatabase.getInstance().getReference(Utils.USERS_DB)
                .child(AppHelper.getUID()).child(Utils.USER_INFO)
                .child("phone");
    }
}
