package com.kodemerah.android.citraclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.HashMap;

public class MenuActivity extends AppCompatActivity {

    SessionManager session;

    AlertDialogManager alert = new AlertDialogManager();

    String name, email, id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        name = user.get(SessionManager.KEY_NAME);
        email = user.get(SessionManager.KEY_EMAIL);
        id = user.get(SessionManager.KEY_ID);
    }

    public void pesantaksiClick(View v){
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void myorderClick(View v){
        Intent i = new Intent(this, OrderActivity.class);
        startActivity(i);
    }

    public void profileClick(View v){
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void signoutClick(View v){session.logoutUser();}

}
