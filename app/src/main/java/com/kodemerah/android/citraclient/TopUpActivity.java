package com.kodemerah.android.citraclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class TopUpActivity extends AppCompatActivity {

    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    public static final String TOPUP_URL = "http://dindrabastari.esy.es/citra/index.php/mobile/topup_voucher";
    public static final String TOPUP_KODE = "kode";
    public static final String TOPUP_ID = "id";
    private String[] topup = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Top Up Saldo");

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        topup[0] = user.get(SessionManager.KEY_ID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void submittopupClick(View v){
        TextView kodeText = (TextView)findViewById(R.id.txtKode);
        topup[1] = kodeText.getText().toString();
        topupLogic();
    }

    public void notifClick(View v){
        NavUtils.navigateUpFromSameTask(this);
    }

    private void topupLogic(){
        class TopupLogic extends AsyncTask<String,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TopUpActivity.this, "Checking Voucher", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if(s.equals("sukses")){
                    Intent i = new Intent(getApplicationContext(), NotificationActivity.class);
                    i.putExtra("EXTRA_TEXT","Selamat!\nVoucher anda berhasil digunakan!");
                    startActivity(i);
                    finish();
                }else{
                    alert.showAlertDialog(TopUpActivity.this, "Failure..", "Check your voucher code", false);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String id = params[0];

                HashMap<String,String> data = new HashMap<>();
                data.put(TOPUP_ID, params[0]);
                data.put(TOPUP_KODE, params[1]);

                String result = rh.sendPostRequest(TOPUP_URL,data);
                if(!result.equals("Error Registering")){
                    result = cancelJSONParse(result);
                }
                return result;
            }
        }

        TopupLogic lol = new TopupLogic();
        lol.execute(topup);
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
