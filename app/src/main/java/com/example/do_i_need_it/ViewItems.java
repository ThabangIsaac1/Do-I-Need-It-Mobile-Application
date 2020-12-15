package com.example.do_i_need_it;
/**
 * The java class ViewItems Extends Fragment
 * This fragment displays products added in Firestore.
 * Note application runs on a Nexus 5X API 30
 *
 * @author Thabang Fenge Isaka
 * @version 1.0
 * @since 2020-11-16
 */
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.do_i_need_it.utils.SimplePlacePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ViewItems extends Fragment  {

    //Declaration of components and variables
    private static final Object TAG =null ;
    private Button selectLocation;
    public static final int REQUEST_CODE = 1;
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    private EditText productName_txt, productDescription_txt, productPrice_txt, productSite_txt;
    private static final int ADDRESS_PICKER_REQUEST = 1020;
    private Uri imageUri;
    private ViewPager viewPager;
    private ArrayList<MyModel> modelArrayList;
    private MyAdapter myAdapter;

    String userId;
    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    FloatingActionButton popupBtn;
    ImageButton pickPlace;
    CircleImageView productImage;
    String productName, productDescription, productPrice, productSite;

    TextView note;
    String apiKey;
    String country;
    String language;
    String[]supportedAreas;
    FirebaseFirestore mFirestore;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_view_items, container, false);


        Dialog dialog = new Dialog(getContext());
        //Instantiate Database and authentication Services


        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        userId = fireAuth.getCurrentUser().getUid();

        viewPager = view1.findViewById(R.id.viewPager);
        popupBtn = view1.findViewById(R.id.fab);
        dialog.setTitle("Add Item");

        // inflate the layout
        dialog.setContentView(R.layout.custom_map);


        selectLocation = dialog.findViewById(R.id.mapSelect);
        productName_txt = dialog.findViewById(R.id.productName);
        productDescription_txt = dialog.findViewById(R.id.productDescription);
        productSite_txt = dialog.findViewById(R.id.productSite);
        productPrice_txt = dialog.findViewById(R.id.productPrice);
        productImage = dialog.findViewById(R.id.productImage);
        note = view1.findViewById(R.id.note);



        //Open add product Dialog
        popupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Display the dialog
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);


            }

        });


//Button to open map activity
selectLocation.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {


        productName = productName_txt.getText().toString().trim();
        productDescription = productDescription_txt.getText().toString().trim();
        productPrice = productPrice_txt.getText().toString().trim();
        productSite = productSite_txt.getText().toString().trim();

        //Validation Of Form data
        if (TextUtils.isEmpty(productName)) {
            Toast.makeText(getContext(), "Enter Product Name!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(productDescription)) {
            Toast.makeText(getContext(), "Enter Product Description!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productPrice)) {
            Toast.makeText(getContext(), "Enter Product Price!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(productSite)) {
            Toast.makeText(getContext(), "Enter Product Website Url!", Toast.LENGTH_SHORT).show();
            return;
        }

       if (TextUtils.isEmpty(imageUri.toString())){

           Toast.makeText(getContext(), "Please Upload An image", Toast.LENGTH_SHORT).show();
           return;
       }

        Intent intent = new Intent(getContext(), MapSelectLocation.class);
        intent.putExtra("product_name", productName);
        intent.putExtra("product_description", productDescription);
        intent.putExtra("product_price", productPrice);
        intent.putExtra("product_site", productSite);
        intent.putExtra("image-uri", imageUri.toString());
        startActivityForResult(intent, SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE);
        dialog.dismiss();

        System.out.println("Image here" + imageUri);


    }
});


        //select image
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();

            }


        });

        loadCards();


        return view1;
    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), 1);
    }



    private void loadCards() {

        //Load array and great firestore instance.
        modelArrayList = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();

        Query query = mFirestore.collection("products").whereEqualTo("product_owner", fireAuth.getCurrentUser().getEmail());
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        
                        //Fetch from firebase all the documents
                        note.setVisibility(View.VISIBLE);
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(String.valueOf(TAG), document.getId() + " => " + document.getData());
                            String prodId = document.getId().toString();
                            String date = document.getString("date_added");
                            String productName = document.getString("product_name");
                            String owner = document.getString("product_owner");
                            String product_description = document.getString("product_description");
                            String product_site = document.getString("product_site");
                            String product_price = document.getString("product_price");
                            String user_id = document.getString("user_id");
                            String product_address = document.getString("product_address");
                            int lat = document.getLong("latitude").intValue();
                            String latitude = String.valueOf(lat);
                            int longi = document.getLong("longitude").intValue();
                            String longitude = String.valueOf(longi);
                            String image_url = document.getString("image_url");
                            String status = document.getString("status");

                            MyModel products = new MyModel(prodId,productName, product_description, date, product_address, owner, user_id, product_site, image_url, latitude, longitude, product_price,status);

                            //Add products to the list
                            modelArrayList.add(products);
                            myAdapter = new MyAdapter(getContext(), modelArrayList);
                            viewPager.setAdapter(myAdapter);
                            note.setVisibility(View.INVISIBLE);
                            viewPager.setPadding(100, 0, 100, 0);


                        }
                        Log.d(String.valueOf(TAG), "Array Items => " + modelArrayList.size());
                    } else {
                        Log.d(String.valueOf(TAG), "Error getting documents: ", task.getException());
                    }
                });


    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Glide.with(this).load(imageUri)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(productImage);

        }



    }

}
