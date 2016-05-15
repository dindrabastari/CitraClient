package com.kodemerah.android.citraclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void pesantaksiClick(View v){startActivity(new Intent(this, ProfileActivity.class));}
    public void myorderClick(View v){startActivity(new Intent(this, OrderActivity.class));}
    public void profileClick(View v){startActivity(new Intent(this, ProfileActivity.class));}
    public void signoutClick(View v){startActivity(new Intent(this, ProfileActivity.class));}

}
