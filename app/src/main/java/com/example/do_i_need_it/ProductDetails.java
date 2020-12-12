package com.example.do_i_need_it;
/**
 * The java class ProductDetails Extends AppCompatActivity
 * This displays the product details, deletes a product and shares a product with other apps.
 * Note application runs on a Nexus 5X API 30
 *
 * @author Thabang Fenge Isaka
 * @version 1.0
 * @since 2020-11-16
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProductDetails extends AppCompatActivity implements OnMapReadyCallback {
    //Declarations Of Vraiables
    TextView productname, websitelink;
    ImageView displayimage;
    ImageButton delete, share;
    Button  purchase;
    String address;
    GoogleMap Map;
    private View mapView;
    Double latitiude, longitude;


    //firebase
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productdetails);


        //Initialize views
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.display_item_location);
        mapFragment.getMapAsync(this);

        delete = findViewById(R.id.deletebtn);
        share = findViewById(R.id.sharebtn);
        purchase = findViewById(R.id.purchased_btn);

        productname = findViewById(R.id.displayproductname);
        websitelink = findViewById(R.id.displayproducturl);
        displayimage = findViewById(R.id.imagesdisplay);

        //Initialize Firebase
        mStorageRef = FirebaseStorage.getInstance().getReference();
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        userId = fireAuth.getCurrentUser().getUid();


        //Get data from The Adapter
        Intent intent = getIntent();
        String productid = intent.getStringExtra("product_id");
        String display_product_name = intent.getStringExtra("display_product_name");
        String display_url = intent.getStringExtra("display_url");
        String display_image = intent.getStringExtra("display_image");
        address = intent.getStringExtra("display_address");
        latitiude = Double.valueOf(intent.getStringExtra("latitude"));
        longitude = Double.valueOf(intent.getStringExtra("longitude"));

        //Set Selected Product to be viewed
        productname.setText(display_product_name);
        websitelink.setText(display_url);
        Glide.with(ProductDetails.this).load(display_image)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .disallowHardwareConfig()
                .into(displayimage);

        //Click url to open browser
        websitelink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(display_url));
                startActivity(browserIntent);
            }
        });


        //Delete Item
        delete.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                userId = fireAuth.getCurrentUser().getUid();

                DocumentReference product = fireStore.collection("products").document(productid);
                product.delete();


                DocumentReference deleteditem = fireStore.collection("deleted_products").document(productid);
                Map<String, Object> deletedproduct = new HashMap<>();
                deletedproduct.put("status","Deleted");
                deletedproduct.put("owner", fireAuth.getCurrentUser().getEmail());
                deleteditem.set(deletedproduct);


                Toast.makeText(ProductDetails.this, "Product Deleted", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ProductDetails.this, MainActivity2.class);
                startActivity(intent);

            }
        });

        //Share item to another App
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Send Item to another application
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check Out This Item I found   " +"Product Name   " + display_product_name + "\n" + "Image Url  "+ display_url +
                        "\n" + "Location   "+ "\n" + address);

                startActivity(sendIntent);
            }
        });

        //Mark Item as Purchased

        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userId = fireAuth.getCurrentUser().getUid();

                DocumentReference product = fireStore.collection("products").document(productid);
                product.delete();


                DocumentReference itemspurchased = fireStore.collection("purchasedItems").document(productid);
                Map<String, Object> purchasedItems = new HashMap<>();
                purchasedItems.put("status","Purchased");
                purchasedItems.put("owner", fireAuth.getCurrentUser().getEmail());
                itemspurchased.set(purchasedItems);

                Toast.makeText(ProductDetails.this, "Finally Purchased", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ProductDetails.this, MainActivity2.class);
                startActivity(intent);

            }
        });



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Map = googleMap;
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitiude, longitude))
                .title(address));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitiude, longitude), 15f));

    }
}
