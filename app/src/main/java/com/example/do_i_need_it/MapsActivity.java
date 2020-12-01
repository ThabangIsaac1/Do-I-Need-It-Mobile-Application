package com.example.do_i_need_it;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int PLACE_PICKER_REQUEST =1;
    private Button addRecordBtn,mapSelector;
    String userId;
    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    ImageButton pickPlace;
    CircleImageView productImage;
    TextView locations;
    private EditText productName_txt, productDescription_txt, productPrice_txt, productSite_txt;
    String productName, productDescription, productPrice, productSite;
    public static final int REQUEST_CODE = 1;
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    private static final int ADDRESS_PICKER_REQUEST = 1020;

    private Uri imageUri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        userId = fireAuth.getCurrentUser().getUid();


        addRecordBtn =findViewById(R.id.upload);
        mapSelector =findViewById(R.id.Change);
        productName_txt = findViewById(R.id.productName);
        productDescription_txt = findViewById(R.id.productDescription);
        productSite_txt = findViewById(R.id.productSite);
        productPrice_txt = findViewById(R.id.productPrice);
        productImage = findViewById(R.id.productImage);
        locations = findViewById(R.id.locations);




        mapSelector.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(MapsActivity.this)
                    ,PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });




        //Locate Image and upload it to database
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();

            }


        });



        //Add product Information to the database
        addRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                productName = productName_txt.getText().toString().trim();
                productDescription = productDescription_txt.getText().toString().trim();
                productSite = productSite_txt.getText().toString().trim();
                productPrice = productPrice_txt.getText().toString().trim();


                //Validation Of Form data
                if (TextUtils.isEmpty(productName)) {
                    Toast.makeText(MapsActivity.this, "Enter Product Name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(productDescription)) {
                    Toast.makeText(MapsActivity.this, "Enter Product Description!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(productSite)) {
                    Toast.makeText(MapsActivity.this, "Enter Product Website Url!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(productPrice)) {
                    Toast.makeText(MapsActivity.this, "Enter Product Price!", Toast.LENGTH_SHORT).show();
                    return;
                }


                //Add product document to firestore for user
                userId = fireAuth.getCurrentUser().getUid();
                DocumentReference product = fireStore.collection("products").document();

                Map<String, Object> productinformation = new HashMap<>();
                productinformation.put("product_name", productName);
                productinformation.put("product_description", productDescription);
                productinformation.put("product_site", productSite);
                productinformation.put("product_price", productPrice);
                productinformation.put("user_id", userId);
                productinformation.put("image_url",imageUri.toString());


                product.set(productinformation).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "Product details successfully stored.");





                        // 5. Confirm success
                        new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Do you really need this item?")
                                .setContentText("Take time and ponder about it Later")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog
                                                .setTitleText("Product Added!")
                                                .setContentText("Your List Is Updated")
                                                .setConfirmText("Check It Out")
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    }
                                })
                                .show();





                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsActivity.this, "Product Failed To add ", Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });


    }
    //Select Image
    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), 1);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;




        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).draggable(true).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Enable the zoom controls for the map
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latLng = marker.getPosition();
                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                try {
                    android.location.Address address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }



    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
            uploadPicture();


        }

        if (requestCode == PLACE_PICKER_REQUEST){
            if (requestCode == RESULT_OK){

                Place place =PlacePicker.getPlace(data,this);
                StringBuilder  stringBuilder = new StringBuilder();
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                stringBuilder.append("LATITUDE :");
                stringBuilder.append(latitude);
                stringBuilder.append("\n");
                stringBuilder.append(longitude);
                locations.setText(stringBuilder.toString());





            }
        }

    }

    private  android.net.Uri uploadPicture() {

        final ProgressDialog pd = new ProgressDialog(MapsActivity.this);
        pd.setTitle("UploadingImage...");
        pd.show();

        userId = fireAuth.getCurrentUser().getUid();

        StorageReference productImages = mStorageRef.child("productImages/" +userId);

        productImages.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Toast.makeText(MapsActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(MapsActivity.this, "image Failed To Upload", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Percentage: " + (int) progressPercent + "%");


            }
        });

return imageUri;
    }


}