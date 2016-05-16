package com.kodemerah.android.citraclient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PesanTaksiActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    private Marker marker;
    private static final int ERROR_DIALOG_MAP = 9990;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (servicesOk()) {
            setContentView(R.layout.activity_map);
        } else {
            setContentView(R.layout.activity_pesan_taksi);
        }
    }

    public boolean servicesOk() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int isAvaliable = googleAPI.isGooglePlayServicesAvailable(this);
        if (isAvaliable != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(isAvaliable)) {
                googleAPI.getErrorDialog(this, isAvaliable, ERROR_DIALOG_MAP).show();
            }

            return false;
        }

        return true;
    }

    private boolean initMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        return (mMap != null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MyMap", "onMapReady");
        mMap = googleMap;
        setUpMap();
    }

    private void setUpMap() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                return;
            }
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setMapToolbarEnabled(false);


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                Log.d("MyMap", "MapClick");

                //remove previously placed Marker
                if (marker != null) {
                    marker.remove();
                }

                //place marker where user just clicked
                marker = mMap.addMarker(new MarkerOptions().position(point).title("Marker")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                Log.d("MyMap", "MapClick After Add Marker");

            }
        });

    }
}
