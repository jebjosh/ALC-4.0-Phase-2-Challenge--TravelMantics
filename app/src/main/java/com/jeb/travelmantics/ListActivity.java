package com.jeb.travelmantics;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ArrayList<TravelDeal> deals;
    private FirebaseDatabase fbdb;

    private DatabaseReference ref;
    private ChildEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu,menu);
        MenuItem insetMenu = menu.findItem(R.id.insert_menu);
        if(FirebaseUtil.isAdmin==true){
            insetMenu.setVisible(true);
        }else{
                insetMenu.setVisible(false);

        }
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.insert_menu:
                Intent intent = new Intent(this, DealActivity.class);
                startActivity(intent);
            return true;

            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                            }
                        });
                FirebaseUtil.detachAuthListener();
                onResume();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachAuthListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUtil.openFbRef("traveldeals",this);
        RecyclerView rvDeals = findViewById(R.id.rvDeals);

        final TravelDealAdapter adapter = new TravelDealAdapter();
        rvDeals.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        rvDeals.setLayoutManager(layoutManager);

        FirebaseUtil.attachAuthListener();
    }

    public void showMenu(){

        invalidateOptionsMenu();
    }
}
