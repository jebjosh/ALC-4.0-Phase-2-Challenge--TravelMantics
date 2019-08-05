package com.jeb.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {

    private FirebaseDatabase fbdb;

    private DatabaseReference ref;

    EditText etTitle, etDesc, etPrice;

    Button btnImage;

    ImageView img;

    TravelDeal deal;


    private static final int PICTURE_RESULT =42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

       // FirebaseUtil.openFbRef("traveldeals",this);
        fbdb = FirebaseUtil.fbdb;
        ref= FirebaseUtil.ref;


        etTitle = findViewById(R.id.etTitle);
        etDesc = findViewById(R.id.etDesc);
        etPrice = findViewById(R.id.etPrice);

        btnImage = findViewById(R.id.btnImage);

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra( Intent.EXTRA_LOCAL_ONLY, true );
                startActivityForResult(intent.createChooser(intent,"Insert Pic"),PICTURE_RESULT);
            }
        });

        img = findViewById(R.id.img);

        Intent intent = getIntent();

        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null){

            deal = new TravelDeal();
        }
        this.deal = deal;

        etTitle.setText(deal.getTitle());
        etDesc.setText(deal.getDesc());
        etPrice.setText(deal.getPrice());
        showImage(deal.getImageUrl());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater =     getMenuInflater();

        inflater.inflate(R.menu.save_menu,menu);
        if(FirebaseUtil.isAdmin){

            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);

        }else{
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.save_menu:

                saveDeal();
                Toast.makeText(this,"Deal Saved",Toast.LENGTH_SHORT).show();
                clean();
                return true;
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this,"Deal Deleted",Toast.LENGTH_SHORT).show();
                backToList();
                return true;
                default:  return super.onOptionsItemSelected(item);  
                
        }
        

    }

    private void saveDeal() {
                   deal.setTitle( etTitle.getText().toString());
                   deal.setPrice(  etPrice.getText().toString());
                   deal.setDesc(etDesc.getText().toString());

                   if (deal.getId()==null) {
                       ref.push().setValue(deal);
                   }else{
                       ref.child(deal.getId()).setValue(deal);
                       backToList();
                   }

    }

    private void deleteDeal(){

        if (deal==null){
            Toast.makeText(this,"Please save the deal before deleting",Toast.LENGTH_SHORT).show();
            return;
        }
        ref.child(deal.getId()).removeValue();

        if (deal.getImageName() !=null &&deal.getImageName().isEmpty()==false){

            StorageReference picRef = FirebaseUtil.storage.getReference().child(deal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Couldn't Delete "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private  void backToList(){

        Intent intent = new Intent (this,ListActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();

    }
    private void clean() {
          etPrice.setText("");
         etDesc.setText("");
        etTitle.setText("");
        etTitle.requestFocus();
    }

    private void enableEditTexts(boolean isEnabled){

        etTitle.setEnabled(isEnabled);
        etDesc.setEnabled(isEnabled);
        etPrice.setEnabled(isEnabled);
       if (isEnabled){
           btnImage.setVisibility(View.VISIBLE);
       }else {
           btnImage.setVisibility(View.GONE);
       }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICTURE_RESULT && resultCode ==RESULT_OK){

            Uri imageUri = data.getData();

            final StorageReference ref = FirebaseUtil.storageRef.child(imageUri.getLastPathSegment());

            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    final String pictureName = taskSnapshot.getStorage().getPath();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            deal.setImageUrl(url);
                            deal.setImageName(pictureName);
                            showImage(url);
                Toast.makeText(getApplicationContext(),pictureName,Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });

//         ref.putFile( imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//             @Override
//             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                 String url = taskSnapshot.getStorage().getDownloadUrl().toString();
//
//                 deal.setImageUrl(url);
//
//                 Toast.makeText(getApplicationContext(),url,Toast.LENGTH_LONG).show();
//             }
        // });

        }
    }

    private void showImage(String url){

        if(url!=null && url.isEmpty()==false){

            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get().load(url).resize(width,width*3/4).centerCrop().into(img);

        }
    }
}
