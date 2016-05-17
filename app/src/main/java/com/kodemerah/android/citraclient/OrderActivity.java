package com.kodemerah.android.citraclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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

public class OrderActivity extends AppCompatActivity{

    RecyclerView rv;

    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    public static final String ORDER_URL = "http://dindrabastari.esy.es/citra/index.php/mobile/get_all_order_customer";
    public static final String ORDER_ID = "id";
    private String order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        order_id = user.get(SessionManager.KEY_ID);

        rv = (RecyclerView) findViewById(R.id.order_rv);




        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("My Order");

        orderLogic();

    }


    class Order {
        String id_pemesanan, id_pelanggan, id_driver, lokasi_jemput, lokasi_tujuan, tanggal_pemesanan, status_pemesanan;

        Order(String id_pemesanan, String id_pelanggan, String id_driver, String lokasi_jemput, String lokasi_tujuan, String tanggal_pemesanan, String status_pemesanan) {
            this.id_pemesanan = id_pemesanan;
            this.id_pelanggan = id_pelanggan;
            this.id_driver = id_driver;
            this.lokasi_jemput = lokasi_jemput;
            this.lokasi_tujuan = lokasi_tujuan;
            this.tanggal_pemesanan = tanggal_pemesanan;
            this.status_pemesanan = status_pemesanan;
        }
    }

    private List<Order> orders;


    public static class OrderViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView orderDestination;
        TextView orderDate;
        ImageView orderStatus;

        OrderViewHolder(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.cv);
            orderDestination = (TextView)itemView.findViewById(R.id.item_name);
            orderDate = (TextView)itemView.findViewById(R.id.item_description);
            orderStatus = (ImageView)itemView.findViewById(R.id.item_icon);
        }
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            int itemPosition = rv.getChildLayoutPosition(view);
            Order item = orders.get(itemPosition);
            Intent i = new Intent(getBaseContext(), OrderDetailActivity.class);
            i.putExtra("EXTRA_ID", String.valueOf(item.id_pemesanan));
            i.putExtra("EXTRA_DESTINATION", String.valueOf(item.lokasi_tujuan));
            i.putExtra("EXTRA_DATE", String.valueOf(item.tanggal_pemesanan));
            i.putExtra("EXTRA_STATUS", String.valueOf(item.status_pemesanan));
            startActivity(i);
        }
    };


    public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder>{

        List<Order> orderList;

        OrderAdapter(List <Order> order){
            this.orderList = order;
        }


        @Override
        public OrderViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_icon_item, viewGroup, false);
            OrderViewHolder pdvh = new OrderViewHolder(v);
            v.setOnClickListener(mOnClickListener);
            return pdvh;
        }

        @Override
        public void onBindViewHolder(OrderViewHolder orderViewHolder, int i) {
            orderViewHolder.orderDestination.setText(orders.get(i).lokasi_tujuan);
            orderViewHolder.orderDate.setText(orders.get(i).tanggal_pemesanan);
            if (orders.get(i).status_pemesanan.equals("SELESAI")) {
                orderViewHolder.orderStatus.setImageResource(R.drawable.ic_check_circle_white_24dp);
            }else{
                orderViewHolder.orderStatus.setImageResource(R.drawable.ic_local_taxi_white_24dp);
            }

        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }

    private void orderLogic(){
        class OrderLogic extends AsyncTask<String,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(OrderActivity.this, "Getting Data", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if(s.equals("kosong")){
                    alert.showAlertDialog(OrderActivity.this, "My Order..", "You have no order history!", false);
                }else{
                    OrderAdapter adapter = new OrderAdapter(orders);
                    rv.setAdapter(adapter);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String orders_id = params[0];

                HashMap<String,String> data = new HashMap<>();
                data.put(ORDER_ID, orders_id);

                String result = rh.sendPostRequest(ORDER_URL,data);
                if(!result.equals("Error Registering")){
                    result = JSONParse(result);
                }
                return result;
            }
        }

        OrderLogic lol = new OrderLogic();
        lol.execute(order_id);
    }

    private String JSONParse(String myJSON){

        String data = "";
        boolean sukses = false;
        try {
            JSONObject jsonRootObject = new JSONObject(myJSON);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("result");
            orders = new ArrayList<>();
            String[] array_data = new String[jsonArray.length()];
            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String status = jsonObject.optString("status").toString();
                String id_pemesanan = jsonObject.optString("id_pemesanan").toString();
                String id_pelanggan = jsonObject.optString("id_pelanggan").toString();
                String id_driver = jsonObject.optString("id_driver").toString();
                String lokasi_tujuan = jsonObject.optString("lokasi_tujuan").toString();
                String lokasi_jemput = jsonObject.optString("lokasi_jemput").toString();
                String tanggal_pemesanan = jsonObject.optString("tanggal_pemesanan").toString();
                String status_pemesanan = jsonObject.optString("status_pemesanan").toString();
                if (!status.equals("sukses")){
                    data = status;
                }else{
                    data = status;
                    orders.add(new Order(id_pemesanan, id_pelanggan, id_driver, lokasi_jemput, lokasi_tujuan, tanggal_pemesanan, status_pemesanan));
                }
            }
        } catch (JSONException e) {e.printStackTrace();}
        return data;
    }

}
