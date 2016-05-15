package com.kodemerah.android.citraclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.HashMap;

public class MenuActivity extends AppCompatActivity {

    SessionManager session;

    AlertDialogManager alert = new AlertDialogManager();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        // name
        String name = user.get(SessionManager.KEY_NAME);
        // email
        String email = user.get(SessionManager.KEY_EMAIL);
    }

    public void pesantaksiClick(View v){startActivity(new Intent(this, ProfileActivity.class));}
    public void myorderClick(View v){startActivity(new Intent(this, OrderActivity.class));}
    public void profileClick(View v){startActivity(new Intent(this, ProfileActivity.class));}
    public void signoutClick(View v){session.logoutUser();}

}
