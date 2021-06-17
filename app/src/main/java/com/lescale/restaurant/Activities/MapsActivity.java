package com.lescale.restaurant.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.MyFirebaseReferences;
import com.lescale.restaurant.Classes.Utils;
import com.lescale.restaurant.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener {

    Location mlocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    public static final String TAG = "MapsActivity";
    GoogleMap mGoogleMap;
    ImageView imageMarker;
    LatLng currentLatLng;
    private UiSettings mUiSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        imageMarker = findViewById(R.id.marker);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        findViewById(R.id.map_fab).setOnClickListener(v -> {
            LatLng latLng = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
            myAdd(latLng);
        });
        mLoation();
    }


    private void mLoation() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(MapsActivity.this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);

        }

        final Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(location -> {
            if (location != null){
                mlocation = location;
                SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
                supportMapFragment.getMapAsync(MapsActivity.this);
            }
        }).addOnFailureListener(e -> Toast.makeText(MapsActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        Log.d(TAG, "onMapReady: ");

        mGoogleMap.setOnCameraIdleListener(this);
        mUiSettings = mGoogleMap.getUiSettings();
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mlocation.getLatitude(), mlocation.getLongitude()), 18.0f));
       
    }

    private void myAdd(@NotNull LatLng latLng) {

        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            final String myAddresses = addressList.get(0).getAddressLine(0);
            if (AppHelper.getCurrentUser() != null) {
                DatabaseReference reference = MyFirebaseReferences.userInfoReference();
                reference.child(Utils.USER_ADDRESS).setValue(myAddresses);
                reference.child(Utils.LONGITUDE).setValue(latLng.longitude);
                reference.child(Utils.LATITUDE).setValue(latLng.latitude);
            }
                        Intent intent1 = new Intent(MapsActivity.this,HomeActivity.class);
                        AppHelper.setSharedPreferences(MapsActivity.this,Utils.LONGITUDE,String.valueOf(latLng.longitude));
                        AppHelper.setSharedPreferences(MapsActivity.this,Utils.LATITUDE,String.valueOf(latLng.latitude));
                        AppHelper.setSharedPreferences(MapsActivity.this,Utils.USER_ADDRESS,myAddresses);
                        intent1.putExtra(Utils.FRAGMENT_CASE,2);
                        intent1.putExtra("a",7);
                        startActivity(intent1);
                        finish();

        } catch (IOException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLoation();
                }
        }
    }


    @Override
    public void onCameraIdle() {
        currentLatLng = mGoogleMap.getCameraPosition().target;
        mlocation.setLongitude(currentLatLng.longitude);
        mlocation.setLatitude(currentLatLng.latitude);
        Log.d(TAG, "onCameraIdle: " + currentLatLng.toString());
    }
}
