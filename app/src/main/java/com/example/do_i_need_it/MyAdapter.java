package com.example.do_i_need_it;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class MyAdapter extends PagerAdapter implements View.OnClickListener  {

    private Context context;
    private ArrayList<MyModel> modelArrayList;
    String userId;
    FirebaseAuth fireAuth;
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    FirebaseFirestore fireStore;
    EditText productName_txt,productDescription_txt,productPrice_txt,productSite_txt,productAddress_txt,latitude_txt,longitude_txt;
    ImageView productImage;


    //image
    Uri imageuri;
    String url;


    public  MyAdapter(Context context, ArrayList<MyModel> modelArrayList){
        this.context = context;
        this.modelArrayList = modelArrayList;


        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        userId = fireAuth.getCurrentUser().getUid();


    }

    @Override
    public int getCount() {
       return  modelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_item,container,false);
        Dialog dialog = new Dialog(context);

        ImageView bannerIv = view.findViewById(R.id.bannerIv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView descriptionTv = view.findViewById(R.id.description);
        Button updateItem = view.findViewById(R.id.btncard) ;


        MyModel model = modelArrayList.get(position);

            String name = model.getName();
            String description = model.getDescription();
            String image = model.getImage().trim();
            Glide.with(context).load(image)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .disallowHardwareConfig()
                    .into(bannerIv);
            titleTv.setText(name);
            descriptionTv.setText(description);



        dialog.setTitle("Edit Item");

        // inflate the layout
        dialog.setContentView(R.layout.update_items);


        productName_txt = dialog.findViewById(R.id.updateproductName);
        productDescription_txt = dialog.findViewById(R.id.updateproductDescription);
        productPrice_txt = dialog.findViewById(R.id.updateproductPrice);
        productSite_txt = dialog.findViewById(R.id.updateproductSite);
        productImage = dialog.findViewById(R.id.updateproductImage);
        productAddress_txt = dialog.findViewById(R.id.updateproductaddress);
        latitude_txt = dialog.findViewById(R.id.updatelatitude);
        longitude_txt = dialog.findViewById(R.id.updatelongitude);









        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, UpdateProductDetails.class);
                intent.putExtra("product_id",model.getProductId());
                intent.putExtra("display_product_name", model.getName());
                intent.putExtra("display_url",model.getSite());
                intent.putExtra("latitude",model.getLatitude());
                intent.putExtra("longitude",model.getLongitude());
                intent.putExtra("display_image",model.getImage());
                intent.putExtra("display_address",model.getAddress());
                context.startActivity(intent);

            }


        });





//View Item properties
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(context, ProductDetails.class);
                intent.putExtra("product_id",model.getProductId());
                intent.putExtra("display_product_name", model.getName());
                intent.putExtra("display_url",model.getSite());
                intent.putExtra("latitude",model.getLatitude());
                intent.putExtra("longitude",model.getLongitude());
                intent.putExtra("display_image",model.getImage());
                intent.putExtra("display_address",model.getAddress());
                context.startActivity(intent);

            }
        });

        updateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(context, UpdateProductDetails.class);
                intent.putExtra("product_id",model.getProductId());
                intent.putExtra("display_product_name", model.getName());
                intent.putExtra("display_product_descrip", model.getDescription());
                intent.putExtra("display_product_price", model.getPrice());
                intent.putExtra("display_url",model.getSite());
                intent.putExtra("latitude",model.getLatitude());
                intent.putExtra("longitude",model.getLongitude());
                intent.putExtra("display_image",model.getImage());intent.putExtra("latitude",model.getLatitude());
                intent.putExtra("display_address",model.getAddress());
                context.startActivity(intent);

            }
        });


  container.addView(view,position);

        return view;
    }




    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }


    @Override
    public void onClick(View v) {

    }


}
