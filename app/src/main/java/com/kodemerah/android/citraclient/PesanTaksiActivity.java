package com.kodemerah.android.citraclient;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PesanTaksiActivity extends AppCompatActivity implements OnMapReadyCallback{


    GoogleMap mMap;
    ImageView ib;
    Button btnSelect;
    EditText searchBox;

    int mode = 1;

    private String lokasi_jemput, lokasi_tujuan;
    private double lat_jemput, lng_jemput, lat_tujuan, lng_tujuan;

    private Marker markerJemput, markerTujuan;
    private static final int ERROR_DIALOG_MAP = 9990;


    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    public static final String ORDER_URL = "http://dindrabastari.esy.es/citra/index.php/mobile/new_order";
    private static final String ORDER_ID_CUSTOMER = "id_customer";
    public static final String ORDER_LOKASI_JEMPUT = "lokasi_jemput";
    public static final String ORDER_LAT_JEMPUT = "lat_jemput";
    public static final String ORDER_LNG_JEMPUT = "lng_jemput";
    public static final String ORDER_LOKASI_TUJUAN = "lokasi_tujuan";
    public static final String ORDER_LAT_TUJUAN = "lat_tujuan";
    public static final String ORDER_LNG_TUJUAN = "lng_tujuan";
    String order_detail[] = new String[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (servicesOk()) {
            setContentView(R.layout.activity_map);
            initMap();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("From");
            ib = (ImageView) findViewById(R.id.ibSelect);
            btnSelect = (Button) findViewById(R.id.btnSelect);
            searchBox = (EditText) findViewById(R.id.searchBox);
        } else {
            setContentView(R.layout.activity_pesan_taksi);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            if(mode == 0){
                NavUtils.navigateUpFromSameTask(this);
            }else{
                mode--;
                if (markerTujuan != null) {
                    markerTujuan.remove();
                }
            }
        }
        return super.onOptionsItemSelected(item);
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

    public void selectClick(View v) throws IOException {
        if (mode == 1){
            if (markerJemput != null) {
                markerJemput.remove();
            }

            markerJemput = mMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(markerJemput.getPosition().latitude, markerJemput.getPosition().longitude, 1);


            if (addresses.size() > 0) {
                String alamat="";
                for (int i =0; i<addresses.get(0).getMaxAddressLineIndex(); i++){
                    if (i == 0){
                        alamat += addresses.get(0).getAddressLine(i);
                    }else{
                        alamat += ", " + addresses.get(0).getAddressLine(i);
                    }
                }
                searchBox.setText(alamat);
                lokasi_jemput = alamat;
                lat_jemput = markerJemput.getPosition().latitude;
                lng_jemput = markerJemput.getPosition().longitude;
            }
        }else{
            if (markerTujuan != null) {
                markerTujuan.remove();
            }

            markerTujuan = mMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(markerTujuan.getPosition().latitude, markerTujuan.getPosition().longitude, 1);

            if (addresses.size() > 0) {
                String alamat="";
                for (int i =0; i<addresses.get(0).getMaxAddressLineIndex(); i++){
                    if (i == 0){
                        alamat += addresses.get(0).getAddressLine(i);
                    }else{
                        alamat += ", " + addresses.get(0).getAddressLine(i);
                    }
                }
                searchBox.setText(alamat);
                lokasi_tujuan = alamat;
                lat_tujuan = markerTujuan.getPosition().latitude;
                lng_tujuan = markerTujuan.getPosition().longitude;
            }
        }


        btnSelect.setVisibility(View.GONE);
        ib.setVisibility(View.GONE);
    }

    public void selectDestinationClick(View v) {
        if (mode == 1) {
            mode = 2;
            Button btnSelectDestination = (Button) findViewById(R.id.btnSelectDest);
            btnSelectDestination.setText("Order Now!");
            getSupportActionBar().setTitle("Destination");
            ib.setVisibility(View.VISIBLE);
            btnSelect.setVisibility(View.VISIBLE);
        }else if(mode == 2){
            session = new SessionManager(getApplicationContext());
            session.checkLogin();
            HashMap<String, String> user = session.getUserDetails();
            String id_customer = user.get(SessionManager.KEY_ID);

            order_detail[0] = id_customer;
            order_detail[1] = lokasi_jemput;
            order_detail[2] = lokasi_tujuan;
            order_detail[3] = String.valueOf(lat_jemput);
            order_detail[4] = String.valueOf(lng_jemput);
            order_detail[5] = String.valueOf(lat_tujuan);
            order_detail[6] = String.valueOf(lng_tujuan);

            orderLogic();
        }
    }

    public void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void gotoLocation(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.animateCamera(cameraUpdate);
    }

    public void searchClick(View v) throws IOException {
        hideSoftKeyboard(v);


        String searchString = searchBox.getText().toString();

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = geocoder.getFromLocationName(searchString, 1);

        if (list.size() > 0) {
            Address adr = list.get(0);

            double lat = adr.getLatitude();
            double lng = adr.getLongitude();
            gotoLocation(lat, lng, 17);
        }

    }

    private void setUpMap() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                return;
            }
        }

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                ib.setVisibility(View.VISIBLE);
                btnSelect.setVisibility(View.VISIBLE);
            }
        });
        mMap.getUiSettings().setMapToolbarEnabled(false);
        gotoLocation(-7.952520, 112.612858, 17);
    }

    private void orderLogic(){
        class OrderLogic extends AsyncTask<String,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(PesanTaksiActivity.this, "Creating New Order", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                String user[]  = s.split("###");
                if(user[0].equals("GAGAL")){
                    alert.showAlertDialog(PesanTaksiActivity.this, "Failure..", "Failed to create new order for you", false);
                } else {
                    Intent i = new Intent(getApplicationContext(), NotificationActivity.class);
                    i.putExtra("EXTRA_TEXT","Selamat!\nPesanan anda berhasil dibuat!");
                    startActivity(i);
                    finish();
                }

            }

            @Override
            protected String doInBackground(String... params) {


                HashMap<String,String> data = new HashMap<>();
                data.put(ORDER_ID_CUSTOMER, params[0]);
                data.put(ORDER_LOKASI_JEMPUT, params[1]);
                data.put(ORDER_LOKASI_TUJUAN, params[2]);
                data.put(ORDER_LAT_JEMPUT, params[3]);
                data.put(ORDER_LNG_JEMPUT, params[4]);
                data.put(ORDER_LAT_TUJUAN, params[5]);
                data.put(ORDER_LNG_TUJUAN, params[6]);


                String result = rh.sendPostRequest(ORDER_URL,data);
                if(!result.equals("Error Registering")){
                    result = JSONParse(result);
                }
                return result;
            }
        }

        OrderLogic lol = new OrderLogic();
        lol.execute(order_detail);
    }

    private String JSONParse(String myJSON){
        String data = "";
        try {
            JSONObject jsonRootObject = new JSONObject(myJSON);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("result");

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String status = jsonObject.optString("status").toString();
                String id_pemesanan = jsonObject.optString("id_pemesanan").toString();
                if (!status.equals("sukses")){
                    data = status;
                }else{
                    data = status + "###" + id_pemesanan;
                }
            }
        } catch (JSONException e) {e.printStackTrace();}
        return data;
    }
}
