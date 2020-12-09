package com.example.do_i_need_it;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

public class ProductDetails extends AppCompatActivity implements OnMapReadyCallback {
    //Declarations Of Vraiables
    TextView productname, websitelink;
    ImageView displayimage;
    ImageButton delete, purchase, share;
    String address;
    GoogleMap Map;
    private View mapView;
    Double latitiude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productdetails);


        //Initialize views

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.display_item_location);
        mapFragment.getMapAsync(this);

        delete = findViewById(R.id.deletebtn);

        productname = findViewById(R.id.displayproductname);
        websitelink = findViewById(R.id.displayproducturl);
        displayimage = findViewById(R.id.imagesdisplay);


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
