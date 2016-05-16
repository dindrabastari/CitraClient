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
    EditText txtEmail, txtPassword;

    // login button
    Button btnLogin, btnSignUp;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    public static final String LOGIN_URL = "http://dindrabastari.esy.es/citra/index.php/mobile/login_customer";
    public static final String LOGIN_EMAIL = "email";
    public static final String LOGIN_PWD = "password";
    private String[] users = new String[2];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Email, Password input text
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);


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
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                users[0] = email;
                users[1] = password;

                // Check if username, password is filled
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    loginLogic();
//                    Toast.makeText(getBaseContext(), email + " - " + password,Toast.LENGTH_LONG).show();
                } else {
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
                } else if(s.equals("Error Registering")){
                    alert.showAlertDialog(LoginActivity.this, "Login Failed..", "Check your connection", false);
                } else {
                    String user[]  = s.split("###");
                    if (user.length > 1) {
                        session.createLoginSession(user[0], user[1], user[2]);
                        Intent i = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(i);
                        finish();
                        Toast.makeText(getApplicationContext(), "Welcome, " + user[1], Toast.LENGTH_LONG).show();
                    }else{
                        alert.showAlertDialog(LoginActivity.this, "Login Failed..", "Check your connection", false);
                    }
                }

            }

            @Override
            protected String doInBackground(String... params) {
                String email = params[0];
                String password = params[1];

                HashMap<String,String> data = new HashMap<>();
                data.put(LOGIN_EMAIL, email);
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
                String email = jsonObject.optString("email").toString();
                String nama = jsonObject.optString("nama").toString();
                if (!status.equals("sukses")){
                    data = status;
                }else{
                    data = id + "###" + nama + "###" + email;
                }

            }
        } catch (JSONException e) {e.printStackTrace();}
        return data;
    }
}
