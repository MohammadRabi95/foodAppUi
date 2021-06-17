package com.lescale.restaurant.Activities;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lescale.restaurant.BottomNavigationFragment.Cart;
import com.lescale.restaurant.BottomNavigationFragment.Category;
import com.lescale.restaurant.BottomNavigationFragment.More;
import com.lescale.restaurant.BottomNavigationFragment.MyMenu;
import com.lescale.restaurant.BottomNavigationFragment.Settings;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.MyFirebaseReferences;
import com.lescale.restaurant.Classes.Utils;
import com.lescale.restaurant.ModelClasses.UserModel;
import com.lescale.restaurant.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 111;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 222;
    public static final String TAG = "Home";
    public static final int ERROR_DIALOG_REQUEST = 333;
    private int itemCount = 0 ;
    private TextView banner;
    private ImageView image_banner;
    private Dialog dialog;
    private ImageView img;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean mLocationPermissionGranted = false;
    private int d_case = 0;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.promodialog);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageButton cancel = dialog.findViewById(R.id.cancel);
        cancel.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        img = dialog.findViewById(R.id.dialog_img);

        loadPromoDialog();

        img.setOnClickListener(v -> {
            AppHelper.setCategory(HomeActivity.this,Utils.PROMOS);
            loadFragment(new MyMenu());
            dialog.dismiss();
        });

        cancel.setOnClickListener(v -> dialog.dismiss());

        banner = findViewById(R.id.banner);

        image_banner = findViewById(R.id.banner_img);

        image_banner.setImageResource(R.drawable.logo);

        FloatingActionButton fab = findViewById(R.id.contact_fab);

        fab.setOnClickListener(v -> {
            Intent intent0 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0022237111777"));
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(HomeActivity.this),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Objects.requireNonNull(HomeActivity.this), new String[]{Manifest.permission.CALL_PHONE}, 1);
                            AppHelper.showCenterToast(HomeActivity.this,getString(R.string.pressagantocall));
            } else {
                startActivity(intent0);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.category_bottomnavigation_menuID:
                    banner.setVisibility(View.GONE);
                    image_banner.setVisibility(View.VISIBLE);
                    loadFragment(new Category());
                    break;

                case R.id.cart_bottomnavigation_menuID:
                    banner.setVisibility(View.VISIBLE);
                    image_banner.setVisibility(View.GONE);
                    banner.setText(R.string.cart);
                    loadFragment(new Cart());
                    break;

                case R.id.more_bottomnavigation_menuID:
                    banner.setVisibility(View.VISIBLE);
                    image_banner.setVisibility(View.VISIBLE);
                    banner.setText("");
                    loadFragment(new More());
                    break;

                case R.id.profile_bottomnavigation_menuID:
                    if (AppHelper.getCurrentUser() != null) {
                        banner.setVisibility(View.VISIBLE);
                        image_banner.setVisibility(View.GONE);
                        banner.setText(AppHelper.getUserName(HomeActivity.this));
                        loadFragment(new Settings());
                    } else {
                        startActivity(new Intent(HomeActivity.this, AuthActivity.class));
                    }
                    break;
            }
            return true;
        });


        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                     runOnUiThread(() -> {
                         if (cartItemCount() != 0){
                             showBadge(HomeActivity.this,bottomNavigationView,R.id.cart_bottomnavigation_menuID
                                     , String.valueOf(cartItemCount()));
                         }else {
                             removeBadge(bottomNavigationView,R.id.cart_bottomnavigation_menuID);
                         }
                     });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AppHelper.getCurrentUser() != null){
            getUserInfo();
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            d_case = getIntent().getExtras().getInt("a");
            int fragment = getIntent().getExtras().getInt(Utils.FRAGMENT_CASE);
            switch (fragment) {
                case 1:
                    banner.setVisibility(View.VISIBLE);
                    image_banner.setVisibility(View.GONE);
                    banner.setText(AppHelper.getCategory(this));
                    loadFragment(new MyMenu());
                    checkMapServices();
                    break;
                case 2:
                    banner.setVisibility(View.VISIBLE);
                    image_banner.setVisibility(View.GONE);
                    banner.setText(R.string.cart);
                    loadFragment(new Cart());
                    checkMapServices();
                    break;
            }
        } else {
            banner.setVisibility(View.GONE);
            image_banner.setVisibility(View.VISIBLE);
            banner.setText(R.string.welcome_to_lescale);
            loadFragment(new Category());
            checkMapServices();
        }
    }


    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.thisapprequiresgpsENBLE)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            loadFragment(new Category());
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if (available == ConnectionResult.SUCCESS) {

            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {

            Log.d(TAG, "isServicesOK: an error came but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            AppHelper.showToast(HomeActivity.this, getString(R.string.cantmapreq));
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    getLocationPermission();
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        AppHelper.showToast(HomeActivity.this,getString(R.string.backpress));

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }


    private void getUserInfo() {

        final DatabaseReference reference = MyFirebaseReferences.userInfoReference();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final UserModel model = dataSnapshot.getValue(UserModel.class);
                    if (model != null) {
                        reference.child(Utils.DEVICE_TOKEN).setValue(FirebaseInstanceId.getInstance().getToken())
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        AppHelper.setUserName(HomeActivity.this, model.getName());
                                        AppHelper.setUserPhone(HomeActivity.this, model.getPhone());
                                        AppHelper.setUserEmail(HomeActivity.this, model.getEmail());
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private int cartItemCount(){

        if (AppHelper.getCurrentUser() != null) {
            DatabaseReference reference = MyFirebaseReferences.cartReference();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        itemCount = (int) dataSnapshot.getChildrenCount();
                    }
                    else {
                        itemCount = 0 ;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return itemCount;
    }

    public void showBadge(Context context, BottomNavigationView
            bottomNavigationView, @IdRes int itemId, String value) {
        removeBadge(bottomNavigationView, itemId);
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        View badge = LayoutInflater.from(context).inflate(R.layout.notification_count, bottomNavigationView, false);
        TextView text = badge.findViewById(R.id.badge_text_view);
        text.setText(value);
        itemView.addView(badge);
    }

    public void removeBadge(@NotNull BottomNavigationView bottomNavigationView, @IdRes int itemId) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        View badge = LayoutInflater.from(HomeActivity.this).inflate(R.layout.notification_count, bottomNavigationView, false);
        TextView text = badge.findViewById(R.id.badge_text_view);
        text.setVisibility(View.GONE);
        if (itemView.getChildCount() == 3) {
            itemView.removeViewAt(2);
        }

    }

    private void loadPromoDialog(){
        if (!AppHelper.getIsPromoDialogShown(HomeActivity.this)) {
            DatabaseReference reference = MyFirebaseReferences.promoDialogReference();
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String url = dataSnapshot.getValue(String.class);
                        Picasso.get().load(url).into(img);
                        dialog.show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            AppHelper.setIsPromoDialogShown(HomeActivity.this,true);
        }
    }

    public int toCart(){
        return d_case;
    }
}
