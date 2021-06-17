package com.lescale.restaurant.Classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


public class AppHelper {

    private static Toast toast;

    public static void showToast(Context context, String msg) {
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(context,msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showCenterToast(Context context, String msg) {
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void setSharedPreferences(Activity activity, String key, String data){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, data);
        editor.apply();
    }

    public static String getSharedPreferences(Activity activity, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return preferences.getString(key, "");
    }

    public static String getLanguage(Activity activity){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return preferences.getString(Utils.LANGUAGE, "");
    }

    public static void setLanguage(Activity activity, String data){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Utils.LANGUAGE, data);
        editor.apply();
    }

    public static String getCategory(Activity activity){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return preferences.getString(Utils.CATEGORY, "");
    }

    public static void setCategory(Activity activity, String data){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Utils.CATEGORY, data);
        editor.apply();
    }

    @NotNull
    public static String getUID(){
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public static void setLocal(@NotNull Activity activity, String lang){

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(configuration,
                activity.getBaseContext().getResources().getDisplayMetrics());
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = activity.
                getSharedPreferences(Utils.SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putString(Utils.LOCAL_LANGUAGE,lang);
        editor.apply();
    }

    public static void loadLocal(@NotNull Activity activity){
        SharedPreferences preferences = activity.getSharedPreferences(Utils.SETTINGS, Context.MODE_PRIVATE);
        String language = preferences.getString(Utils.LOCAL_LANGUAGE, "");
        setLocal(activity,language);
    }

    public static void setUserName(Activity activity, String name){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Utils.USER_NAME, name);
        editor.apply();
    }
    public static void setUserEmail(Activity activity, String email){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Utils.USER_EMAIL, email);
        editor.apply();
    }
    public static void setUserPhone(Activity activity, String phone){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Utils.USER_PHONE, phone);
        editor.apply();
    }
    public static String getUserName(Activity activity){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return preferences.getString(Utils.USER_NAME, "");
    }
    public static String getUserEmail(Activity activity){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return preferences.getString(Utils.USER_EMAIL, "");
    }
    public static String getUserPhone(Activity activity){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return preferences.getString(Utils.USER_PHONE, "");
    }
    public static FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @NotNull
    @Contract(pure = true)
    public static String setPrice(String price){
        Spannable wordtoSpan = new SpannableString(price);
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return wordtoSpan +" MRU";
    }

    public static void setIsAppRanFirstTime(Activity activity, boolean isAppRanFirstTime){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Utils.isAppRanFirstTime, String.valueOf(isAppRanFirstTime));
        editor.apply();
    }

    public static boolean isRanFirstTime(Activity activity){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return Boolean.parseBoolean(preferences.getString(Utils.isAppRanFirstTime, ""));
    }

    @NotNull
    public static MyDialog loadDialog(Activity activity, String msg){
        MyDialog customDialog = new MyDialog(activity,msg);
        customDialog.setCancelable(false);
        Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return customDialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NotNull
    @Contract(pure = true)
    public static String getOrderID() {
        int value = ThreadLocalRandom.current().nextInt(1, 1000);
        int minue = 100000000;
        int time = (int) System.currentTimeMillis();
        int id = value + time - minue;
        String ID = String.valueOf(id);
        return ID.replace("-", " ");
    }

    public static void setIsPromoDialogShown(Activity activity, boolean what){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Utils.isPromoDialogShown, what);
        editor.apply();
    }


    public static boolean getIsPromoDialogShown(Activity activity){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return preferences.getBoolean(Utils.isPromoDialogShown, false);
    }

    public static float getDistanceInKMs(double lat, double lng) {
        float[] results = new float[1];
        Location.distanceBetween(18.105189, -15.979491,
                lat, lng, results);
        return results[0] / 1000;
    }

    @NotNull
    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @NotNull
    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
