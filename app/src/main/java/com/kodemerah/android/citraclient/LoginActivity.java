package com.kodemerah.android.citraclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {


    // Email, password edittext
    EditText txtUsername, txtPassword;

    // login button
    Button btnLogin, btnSignUp;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    public static final String LOGIN_URL = "http://freya.schenker.co.id/tas/index.php/mobile/login";
    public static final String LOGIN_UNAME = "username";
    public static final String LOGIN_PWD = "password";
    private String[] users = new String[2];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Email, Password input text
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        // Login button
        btnLogin = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), RegisterActivity.class));
            }
        });


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username, password from EditText
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();

                // Check if username, password is filled
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    // For testing puspose username, password is checked with sample data
                    // username = test
                    // password = test
                    if (username.equals("test") && password.equals("test")) {

                        // Creating user login session
                        // For testing i am stroing name, email as follow
                        // Use user real data
                        session.createLoginSession("1", "Android Hive", "anroidhive@gmail.com");

                        // Staring MainActivity
                        Intent i = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(i);
                        finish();

                    } else {
                        // username / password doesn't match
                        alert.showAlertDialog(LoginActivity.this, "Login failed..", "Username/Password is incorrect", false);
                    }
                } else {
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter username and password", false);
                }

            }
        });
    }

    private void loginLogic(){
        class LoginLogic extends AsyncTask<String,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this, "Authenticating", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();


//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                if(s.equals("Username dan Password tidak cocok")){
                    alert.showAlertDialog(LoginActivity.this, "Login Failed..", "Username/Password is incorrect", false);
                } else if(s.equals("Maaf, anda tidak memiliki hak akses")){
                    alert.showAlertDialog(LoginActivity.this, "Login Failed..", "Your account have no access", false);
                } else if(s.equals("Error Registering")){
                    alert.showAlertDialog(LoginActivity.this, "Login Failed..", "Check your connection", false);
                } else {
                    String user[]  = s.split("###");
                    session.createLoginSession(user[0], user[1], user[2]);
                    Intent i = new Intent(getApplicationContext(), MenuActivity.class);
                    startActivity(i);
                    finish();
                    Toast.makeText(getApplicationContext(), "Welcome, " + user[0], Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                String username = params[0];
                String password = params[1];

                HashMap<String,String> data = new HashMap<>();
                data.put(LOGIN_UNAME, username);
                data.put(LOGIN_PWD, password);

                String result = rh.sendPostRequest(LOGIN_URL,data);
                if(!result.equals("Error Registering")){
                    result = JSONParse(result);
                }
                return result;
            }
        }

        LoginLogic lol = new LoginLogic();
        lol.execute(users);
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
                String id = jsonObject.optString("id_customer").toString();
                String username = jsonObject.optString("username").toString();
                String name = jsonObject.optString("nama").toString();
                if (!status.equals("sukses")){
                    data = status;
                }else{
                    data = id + "###" + name + "###" + username;
                }

            }
        } catch (JSONException e) {e.printStackTrace();}
        return data;
    }
}
