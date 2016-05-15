package com.kodemerah.android.citraclient;

import android.content.Intent;
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

public class OrderDetailActivity extends AppCompatActivity {

    CardView cvCost,cvProgress, cvDriver;
    TextView txtLocFrom, txtLocDes, txtCost, txtDriverName, txtDriverNo;
    ImageView ivDriverPhoto;
    Button btnCancel, btnPay;

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
        String destination = i.getStringExtra("EXTRA_DESTINATION");
        String date = i.getStringExtra("EXTRA_DATE");
        String status = i.getStringExtra("EXTRA_STATUS");


        if(status.equals("0")){
            cvProgress.setVisibility(View.GONE);
            cvDriver.setVisibility(View.GONE);
            cvCost.setVisibility(View.GONE);
            btnPay.setVisibility(View.GONE);
        }else if (status.equals("1")){
            cvDriver.setVisibility(View.GONE);
            cvCost.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnPay.setVisibility(View.GONE);
        }else if (status.equals("2")) {
            cvProgress.setVisibility(View.GONE);
            cvDriver.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }else if (status.equals("3")) {
            cvProgress.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnPay.setVisibility(View.GONE);
        }
        getSupportActionBar().setTitle("#"+id);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
