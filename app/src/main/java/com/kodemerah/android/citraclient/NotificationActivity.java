package com.kodemerah.android.citraclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Tersandung on 5/17/16.
 */
public class NotificationActivity extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);
        Intent i = getIntent();
        String notif = i.getStringExtra("EXTRA_TEXT");
        TextView tv = (TextView)findViewById(R.id.notifText);
        tv.setText(notif);
    }

    public void notifClick(View v){
        finish();
    }
}
