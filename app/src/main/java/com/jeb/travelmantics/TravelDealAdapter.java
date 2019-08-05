package com.jeb.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TravelDealAdapter extends RecyclerView.Adapter<TravelDealAdapter.TravelDealViewHolder>  {
    private FirebaseDatabase fbdb;
    private DatabaseReference ref;
    private ChildEventListener listener;
    ArrayList<TravelDeal> deals;

    public TravelDealAdapter(){



        fbdb = FirebaseUtil.fbdb;
        ref= FirebaseUtil.ref;
       // FirebaseUtil.openFbRef("traveldeals",this);
        deals = FirebaseUtil.deals;

        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                 TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
                 Log.d("DEAL",travelDeal.getTitle());
                 travelDeal.setId(dataSnapshot.getKey());
               deals.add(travelDeal);
               notifyItemInserted(deals.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ref.addChildEventListener(listener);

    }
    @NonNull
    @Override
    public TravelDealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.rv_row,viewGroup,false);
        return new TravelDealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelDealViewHolder travelDealViewHolder, int i) {
        TravelDeal deal= deals.get(i);
        travelDealViewHolder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }





    public class TravelDealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitle, tvDesc,tvPrice;
        ImageView imgDeal;
        public TravelDealViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imgDeal = itemView.findViewById(R.id.imgDeal);


            itemView.setOnClickListener(this);
        }

        public void bind (TravelDeal deal){
            tvTitle.setText(deal.getTitle());
            tvDesc.setText(deal.getDesc());
            tvPrice.setText(deal.getPrice());
            showImage(deal.getImageUrl());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d("CLick", String.valueOf(position));

            TravelDeal selectedDeal = deals.get(position);

            Intent intent = new Intent(v.getContext(),DealActivity.class);
            intent.putExtra("Deal",selectedDeal);
            v.getContext().startActivity(intent);


        }

        private void showImage(String url){

            if(url!=null && url.isEmpty()==false){

                int width = Resources.getSystem().getDisplayMetrics().widthPixels;
                Picasso.get().load(url).resize(160,160).centerCrop().into(imgDeal);

            }
        }
    }
}
