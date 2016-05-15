package com.kodemerah.android.citraclient;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity{

    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv = (RecyclerView) findViewById(R.id.order_rv);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("My Order");

        initializeData();

        OrderAdapter adapter = new OrderAdapter(orders);
        rv.setAdapter(adapter);
    }


    class Order {
        int id;
        String destination;
        String date;
        int status;

        Order(int id, String destination, String date, int status) {
            this.id = id;
            this.destination = destination;
            this.date = date;
            this.status = status;
        }
    }

    private List<Order> orders;

    // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
    private void initializeData(){
        orders = new ArrayList<>();
        orders.add(new Order(2471623,"3 January 2016", "Jl. Kembang Turi No.99c", 0));
        orders.add(new Order(1238764,"3 January 2016", "Jl. Kembang Turi No.99c", 1));
        orders.add(new Order(2187642,"3 January 2016", "Jl. Kembang Turi No.99c", 2));
        orders.add(new Order(2148717,"3 January 2016", "Jl. Kembang Turi No.99c", 3));
        orders.add(new Order(1234125,"3 January 2016", "Jl. Kembang Turi No.99c", 3));
        orders.add(new Order(8763236,"3 January 2016", "Jl. Kembang Turi No.99c", 3));
        orders.add(new Order(5237617,"3 January 2016", "Jl. Kembang Turi No.99c", 3));

    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView orderDestination;
        TextView orderDate;
        ImageView orderStatus;

        OrderViewHolder(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.cv);
            orderDestination = (TextView)itemView.findViewById(R.id.item_description);
            orderDate = (TextView)itemView.findViewById(R.id.item_name);
            orderStatus = (ImageView)itemView.findViewById(R.id.item_icon);
        }
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            int itemPosition = rv.getChildLayoutPosition(view);
            Order item = orders.get(itemPosition);
            Intent i = new Intent(getBaseContext(), OrderDetailActivity.class);
            i.putExtra("EXTRA_ID", String.valueOf(item.id));
            i.putExtra("EXTRA_DESTINATION", String.valueOf(item.destination));
            i.putExtra("EXTRA_DATE", String.valueOf(item.date));
            i.putExtra("EXTRA_STATUS", String.valueOf(item.status));
            startActivity(i);
        }
    };


    public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder>{

        List<Order> orderList;

        OrderAdapter(List <Order> profileDet){
            this.orderList = profileDet;
        }



        @Override
        public OrderViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_icon_item, viewGroup, false);
            OrderViewHolder pdvh = new OrderViewHolder(v);
            v.setOnClickListener(mOnClickListener);
            return pdvh;
        }

        @Override
        public void onBindViewHolder(OrderViewHolder profileDetailViewHolder, int i) {
            profileDetailViewHolder.orderDestination.setText(orders.get(i).destination);
            profileDetailViewHolder.orderDate.setText(orders.get(i).date);
            if (orders.get(i).status == 3) {
                profileDetailViewHolder.orderStatus.setImageResource(R.drawable.ic_check_circle_white_24dp);
            }else{
                profileDetailViewHolder.orderStatus.setImageResource(R.drawable.ic_local_taxi_white_24dp);
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

}
