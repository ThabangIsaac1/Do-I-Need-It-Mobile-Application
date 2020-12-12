package com.example.do_i_need_it;
/**
 * The java class UpdateProductDetails Extends AppCompatActivity
 * This class updates product information for a user.
 * Note application runs on a Nexus 5X API 30
 *
 * @author Thabang Fenge Isaka
 * @version 1.0
 * @since 2020-11-16
 */
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UpdateProductDetails  extends AppCompatActivity {

    //D eclaration of Variables  and Components to be used.
    EditText productName_txt,productDescription_txt,productPrice_txt,productSite_txt,productAddress_txt,latitude_txt,longitude_txt;
    ImageView productImage;
    String userId;
    FirebaseAuth fireAuth;
    StorageReference mStorageRef;
    FirebaseStorage storage;
    FirebaseFirestore fireStore;

    //image
    Uri imageuri;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_items);


        //Firebase Instances
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        userId = fireAuth.getCurrentUser().getUid();

        Button updateItem = findViewById(R.id.updateitem) ;


        //Instantiate views
        productName_txt = findViewById(R.id.updateproductName);
        productDescription_txt = findViewById(R.id.updateproductDescription);
        productPrice_txt = findViewById(R.id.updateproductPrice);
        productSite_txt = findViewById(R.id.updateproductSite);
        productImage = findViewById(R.id.updateproductImage);
        productAddress_txt = findViewById(R.id.updateproductaddress);
        latitude_txt = findViewById(R.id.updatelatitude);
        longitude_txt = findViewById(R.id.updatelongitude);



        Intent intent = getIntent();
        productName_txt.setText(intent.getStringExtra("display_product_name"));
        productDescription_txt.setText(intent.getStringExtra("display_product_descrip"));
        productPrice_txt.setText(intent.getStringExtra("display_product_price"));
        productSite_txt.setText(intent.getStringExtra("display_url"));
        productAddress_txt.setText(intent.getStringExtra("display_address"));
        latitude_txt.setText(intent.getStringExtra("latitude"));
        longitude_txt.setText(intent.getStringExtra("longitude"));
        String display_image = intent.getStringExtra("display_image");
        String product_id = intent.getStringExtra("product_id");





        Glide.with(UpdateProductDetails.this).load(display_image)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .disallowHardwareConfig()
                .into(productImage);



        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        //Collect prodcut infromation and update it on Firebase
        updateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText productName_txt = findViewById(R.id.updateproductName);
                EditText  productDescription_txt = findViewById(R.id.updateproductDescription);
                EditText  productPrice_txt = findViewById(R.id.updateproductPrice);
                EditText productSite_txt = findViewById(R.id.updateproductSite);
                EditText productAddress_txt = findViewById(R.id.updateproductaddress);
                EditText latitude_txt = findViewById(R.id.updatelatitude);
                EditText longitude_txt = findViewById(R.id.updatelongitude);



                String productName = productName_txt.getText().toString();
                String productDescription = productDescription_txt.getText().toString();
                String productPrice = productPrice_txt.getText().toString();
                String productSite = productSite_txt.getText().toString();
                String productAddress = productAddress_txt.getText().toString();
                String latitudes = latitude_txt.getText().toString();
                String longitudes = longitude_txt.getText().toString();
                Double latitude = Double.valueOf(latitudes);
                Double longitude = Double.valueOf(longitudes);



                fireStore = FirebaseFirestore.getInstance();
                userId = fireAuth.getCurrentUser().getUid();
                Map<String, Object> updateproductinformation = new HashMap<>();
                updateproductinformation.put("product_name", productName);
                updateproductinformation.put("product_description", productDescription);
                updateproductinformation.put("product_price", productPrice);
                updateproductinformation.put("product_site", productSite);
                updateproductinformation.put("product_address", productAddress);
                updateproductinformation.put("latitude", latitude);
                updateproductinformation.put("longitude", longitude);



                //Update Products to Firestore
                fireStore.collection("products").document(product_id).update(updateproductinformation).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // 5. Confirm success
                        new SweetAlertDialog(UpdateProductDetails.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Update Product Information")
                                .setContentText("Tap to update")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog
                                                .setTitleText("Product Update!")
                                                .setContentText("Your List Is Updated")
                                                .setConfirmText("Check It Out")
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);




                                        Intent intent = new Intent(UpdateProductDetails.this, MainActivity2.class);
                                        startActivity(intent);
                                    }

                                })
                                .show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProductDetails.this, "Product Failed To Add", Toast.LENGTH_SHORT).show();

                    }
                });



            }
        });



    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageuri = data.getData();

            Glide.with(this).load(imageuri)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(productImage);

        }



    }
}