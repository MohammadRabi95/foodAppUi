package com.lescale.restaurant.Classes;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lescale.restaurant.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendNotification {

    private static final String TAG = "SendNotification";

    public static void sendNotification(Activity activity, String name){

        String serverKey = "key=AAAA1aAwFkM:APA91bFkHhEWJIz4UZYtkbonPg9adY9R0Q3lqoBmVjeh4_-lW2jZIdwyoLax6hKD2i6mlQitIO57D2wbNOniCSKOL6X0SAMh-LSuWK2an1tuCzbZXOWc0-5IPdSZxI6ktGh-43b-Eoky";

        String URL = "https://fcm.googleapis.com/fcm/send";

        RequestQueue mRequestQue = Volley.newRequestQueue(activity);

        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + "Admin");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", "New Order");
            notificationObj.put("body", "New Order is received from " + name);
            json.put("notification", notificationObj);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    response ->
                Log.d(TAG, "onResponse: "),
                    error -> Log.d(TAG, "onError: " + error.networkResponse)
            ) {
                @NotNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", serverKey);
                    return header;
                }
            };
            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
