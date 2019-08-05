package com.jeb.travelmantics;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class FirebaseUtil {

    public static  ArrayList<TravelDeal> deals;
    public static FirebaseDatabase fbdb;
    public static FirebaseUtil firebaseUtil;
    public static DatabaseReference ref;
    public static FirebaseAuth auth;
    public static FirebaseAuth.AuthStateListener authStateListener;
    public  static FirebaseStorage storage;
public static StorageReference  storageRef;
    private static final int RC_SIGN_IN = 123;
    private static ListActivity caller;
    public static boolean isAdmin;

    private FirebaseUtil(){}

    public  static  void openFbRef(String dbref, final ListActivity callerActivity){

        if (firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            fbdb = FirebaseDatabase.getInstance();
            caller = callerActivity;
            auth = FirebaseAuth.getInstance();
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    if(firebaseAuth.getCurrentUser() == null){
                        FirebaseUtil.signIn();
                    }
else{
                        String userId = firebaseAuth.getUid();
                        checkAdmin(userId);
                        Toast.makeText(callerActivity.getBaseContext(),userId,Toast.LENGTH_LONG).show();}

                   Toast.makeText(callerActivity.getBaseContext(),"Welcome back",Toast.LENGTH_LONG).show();
                }
            };

            connectStorage();

        }
        deals = new ArrayList<TravelDeal>();
        ref = fbdb.getReference().child(dbref);
    }

    private static void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),

                new AuthUI.IdpConfig.GoogleBuilder().build());




        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }


    private  static void checkAdmin(String uid){

       FirebaseUtil.isAdmin=false;
        DatabaseReference refAd = fbdb.getReference().child("admins").child(uid);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin=true;
                caller.showMenu();

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

        refAd.addChildEventListener(listener);


    }

    public static void attachAuthListener(){

        auth.addAuthStateListener(authStateListener);

    }
    public static void detachAuthListener(){

        auth.removeAuthStateListener(authStateListener);

    }

    public static void connectStorage(){

        storage = FirebaseStorage.getInstance();

        storageRef = storage.getReference().child("deals_pics");
    }
}
