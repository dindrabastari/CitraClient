package com.kodemerah.android.citraclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class OrderDetailActivity extends AppCompatActivity {

    CardView cvCost,cvProgress, cvDriver;
    TextView txtLocFrom, txtLocDes, txtCost, txtDriverName, txtDriverNo;
    ImageView ivDriverPhoto;
    Button btnCancel, btnPay;


    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    public static final String ORDER_URL = "http://dindrabastari.esy.es/citra/index.php/mobile/get_order_customer";
    public static final String CANCEL_ORDER_URL = "http://dindrabastari.esy.es/citra/index.php/mobile/cancel_order";
    public static final String ORDER_ID = "id";
    private String orders_id;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtLocFrom = (TextView) findViewById(R.id.item_location_from);
        txtLocDes = (TextView) findViewById(R.id.item_location_destination);
        txtCost = (TextView) findViewById(R.id.item_cost);
        txtDriverName = (TextView) findViewById(R.id.item_driver_name);
        txtDriverNo = (TextView) findViewById(R.id.item_driver_nopol);

        ivDriverPhoto = (ImageView) findViewById(R.id.item_driver_photo);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnPay = (Button) findViewById(R.id.btnPay);

        cvCost = (CardView) findViewById(R.id.cv_cost);
        cvDriver = (CardView) findViewById(R.id.cv_driver);
        cvProgress = (CardView) findViewById(R.id.cv_progress);

        String id = i.getStringExtra("EXTRA_ID");

        orders_id = id;

        orderLogic();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setLayout(String id_pemesanan, String biaya, String nama_driver, String lokasi_jemput, String lokasi_tujuan, String nopol, String status_pemesanan, String foto_driver){
        if(status_pemesanan.equals("BELUM TO")){
            txtLocFrom.setText(lokasi_jemput);
            txtLocDes.setText(lokasi_tujuan);

            cvProgress.setVisibility(View.GONE);
            cvDriver.setVisibility(View.GONE);
            cvCost.setVisibility(View.GONE);
            btnPay.setVisibility(View.GONE);
        }else if (status_pemesanan.equals("SUDAH TO")){
            txtLocFrom.setText(lokasi_jemput);
            txtLocDes.setText(lokasi_tujuan);

            cvDriver.setVisibility(View.GONE);
            cvCost.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnPay.setVisibility(View.GONE);
        }else if (status_pemesanan.equals("SAMPAI")) {
            txtLocFrom.setText(lokasi_jemput);
            txtLocDes.setText(lokasi_tujuan);
            txtCost.setText(biaya);

            cvProgress.setVisibility(View.GONE);
            cvDriver.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }else if (status_pemesanan.equals("SELESAI")) {
            txtLocFrom.setText(lokasi_jemput);
            txtLocDes.setText(lokasi_tujuan);
            txtCost.setText(biaya);
            txtDriverName.setText(nama_driver);
            txtDriverNo.setText(nopol);
            ivDriverPhoto.setImageURI(Uri.parse(foto_driver));

            cvProgress.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnPay.setVisibility(View.GONE);
        }
        getSupportActionBar().setTitle("#" + id_pemesanan);
    }

    public void cancelorderClick(View v){
        cancelOrderLogic();
    }

    private void orderLogic(){
        class OrderLogic extends AsyncTask<String,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(OrderDetailActivity.this, "Getting Data", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                String[] orderDet = s.split("###");
                String id_pemesanan = orderDet[0];
                String lokasi_jemput = String.valueOf(orderDet[1]);
                String lokasi_tujuan = String.valueOf(orderDet[2]);
                String status_pemesanan = orderDet[3];
                String biaya = "", nama_driver = "", foto_driver = "", nopol = "";
                if (status_pemesanan.equals("SAMPAI")) {
                    biaya = orderDet[4];
                }else if (status_pemesanan.equals("SELESAI")) {
                    biaya = orderDet[4];
                    foto_driver = orderDet[5];
                    nopol = orderDet[6];
                    nama_driver = orderDet[7];
                }


                setLayout(id_pemesanan, biaya, nama_driver, lokasi_jemput, lokasi_tujuan, nopol, status_pemesanan, foto_driver);
            }

            @Override
            protected String doInBackground(String... params) {
                String id = params[0];

                HashMap<String,String> data = new HashMap<>();
                data.put(ORDER_ID, id);

                String result = rh.sendPostRequest(ORDER_URL,data);
                if(!result.equals("Error Registering")){
                    result = JSONParse(result);
                }
                return result;
            }
        }

        OrderLogic lol = new OrderLogic();
        lol.execute(orders_id);
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
                String biaya = jsonObject.optString("biaya").toString();
                String nama_driver = jsonObject.optString("nama_driver").toString();
                String foto_driver = jsonObject.optString("foto_driver").toString();
                String nopol = jsonObject.optString("nopol").toString();
                String lokasi_jemput = jsonObject.optString("lokasi_jemput").toString();
                String lokasi_tujuan = jsonObject.optString("lokasi_tujuan").toString();
                String status_pemesanan = jsonObject.optString("status_pemesanan").toString();
                if (!status.equals("sukses")){
                    data = status;
                }else{
                    if (status_pemesanan.equals("SAMPAI")) {
                        data = id_pemesanan + "###" + lokasi_jemput + "###" + lokasi_tujuan + "###" + status_pemesanan + "###" + biaya;
                    }else if (status_pemesanan.equals("SELESAI")) {
                        data = id_pemesanan + "###" + lokasi_jemput + "###" + lokasi_tujuan + "###" + status_pemesanan + "###" + biaya + "###" + foto_driver + "###" + nopol + "###" + nama_driver;
                    }else {
                        data = id_pemesanan + "###" + lokasi_jemput + "###" + lokasi_tujuan + "###" + status_pemesanan;
                    }
                }
            }
        } catch (JSONException e) {e.printStackTrace();}
        return data;
    }

    private void cancelOrderLogic(){
        class OrderLogic extends AsyncTask<String,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(OrderDetailActivity.this, "Cancelling Order", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if(s.equals("sukses")){
                    finish();
                }else{
                    alert.showAlertDialog(OrderDetailActivity.this, "Failure..", "Failed to cancel your order", false);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String id = params[0];

                HashMap<String,String> data = new HashMap<>();
                data.put(ORDER_ID, id);

                String result = rh.sendPostRequest(CANCEL_ORDER_URL,data);
                if(!result.equals("Error Registering")){
                    result = cancelJSONParse(result);
                }
                return result;
            }
        }

        OrderLogic lol = new OrderLogic();
        lol.execute(orders_id);
    }

    private String cancelJSONParse(String myJSON){
        String data = "";
        try {
            JSONObject jsonRootObject = new JSONObject(myJSON);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("result");

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String status = jsonObject.optString("status").toString();
                data = status;
            }
        } catch (JSONException e) {e.printStackTrace();}
        return data;
    }
}
