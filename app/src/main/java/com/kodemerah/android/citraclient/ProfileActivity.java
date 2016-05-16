package com.kodemerah.android.citraclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    RecyclerView rv;

    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    public static final String PROFILE_URL = "http://dindrabastari.esy.es/citra/index.php/mobile/profile_customer";
    public static final String PROFILE_ID = "id";
    private String user_id;

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
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv = (RecyclerView) findViewById(R.id.provile_rv);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(SessionManager.KEY_ID);

        profileLogic();


    }

    public void topupClick(View v){
        startActivity(new Intent(this, TopUpActivity.class));
    }

    class ProfileDetail {
        String name;
        String value;
        int icon;

        ProfileDetail(String name, String value, int icon) {
            this.name = name;
            this.value = value;
            this.icon = icon;
        }
    }

    private List<ProfileDetail> profileDetails;

    // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
    private void initializeData(){
        profileDetails = new ArrayList<>();
        profileDetails.add(new ProfileDetail("Alamat E-mail", "danindbas@yahoo.co.id", R.drawable.ic_email_black_24dp));
        profileDetails.add(new ProfileDetail("Nomor Handphone", "087779222236", R.drawable.ic_phone_black_24dp));
        profileDetails.add(new ProfileDetail("Saldo", "Rp. 124.000,00-", R.drawable.ic_attach_money_black_24dp));
    }

    public static class ProfileDetailViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView profileDetailName;
        TextView profileDetailValue;
        ImageView profileDetailIcon;

        ProfileDetailViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            profileDetailName = (TextView)itemView.findViewById(R.id.item_description);
            profileDetailValue = (TextView)itemView.findViewById(R.id.item_name);
            profileDetailIcon = (ImageView)itemView.findViewById(R.id.item_icon);
        }
    }

    public class ProfileAdapter extends RecyclerView.Adapter<ProfileDetailViewHolder>{

        List<ProfileDetail> profileDet;

        ProfileAdapter(List <ProfileDetail> profileDet){
            this.profileDet = profileDet;
        }

        @Override
        public ProfileDetailViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.icon_text_item, viewGroup, false);
            ProfileDetailViewHolder pdvh = new ProfileDetailViewHolder(v);
            return pdvh;
        }

        @Override
        public void onBindViewHolder(ProfileDetailViewHolder profileDetailViewHolder, int i) {
            profileDetailViewHolder.profileDetailName.setText(profileDetails.get(i).name);
            profileDetailViewHolder.profileDetailValue.setText(profileDetails.get(i).value);
            profileDetailViewHolder.profileDetailIcon.setImageResource(profileDetails.get(i).icon);
        }

        @Override
        public int getItemCount() {
            return profileDet.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }

    private void profileLogic(){
        class ProfileLogic extends AsyncTask<String,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ProfileActivity.this, "Getting Data", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                String[] profile = s.split("###");
                getSupportActionBar().setTitle(profile[1]);

                profileDetails = new ArrayList<>();
                profileDetails.add(new ProfileDetail("Alamat E-mail", profile[2], R.drawable.ic_email_black_24dp));
                profileDetails.add(new ProfileDetail("Nomor Handphone", profile[4], R.drawable.ic_phone_black_24dp));
                profileDetails.add(new ProfileDetail("Saldo", profile[3], R.drawable.ic_attach_money_black_24dp));

                ProfileAdapter adapter = new ProfileAdapter(profileDetails);
                rv.setAdapter(adapter);
            }

            @Override
            protected String doInBackground(String... params) {
                String id = params[0];

                HashMap<String,String> data = new HashMap<>();
                data.put(PROFILE_ID, id);

                String result = rh.sendPostRequest(PROFILE_URL,data);
                if(!result.equals("Error Registering")){
                    result = JSONParse(result);
                }
                return result;
            }
        }

        ProfileLogic lol = new ProfileLogic();
        lol.execute(user_id);
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
                String saldo = jsonObject.optString("saldo").toString();
                String phone = jsonObject.optString("phone").toString();

                if (!status.equals("sukses")){
                    data = status;
                }else{
                    data = id + "###" + nama + "###" + email + "###" + saldo + "###" + phone;
                }

            }
        } catch (JSONException e) {e.printStackTrace();}
        return data;
    }

}
