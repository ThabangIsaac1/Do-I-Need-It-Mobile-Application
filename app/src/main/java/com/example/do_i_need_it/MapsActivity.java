package com.example.do_i_need_it;
/**
 * The java class MapsActivity Extends FragmentActivity
 * This class where a user adds a product coupled with a GeoTag from a map.
 * Note application runs on a Nexus 5X API 30
 *
 * @author Thabang Fenge Isaka
 * @version 1.0
 * @since 2020-11-16
 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends FragmentActivity {


    //Declarations of components and variables to be used
    private GoogleMap mMap;
    int PLACE_PICKER_REQUEST = 1;
    private Button addRecordBtn, mapSelector;
    public static final int REQUEST_CODE = 1;
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    private static final int ADDRESS_PICKER_REQUEST = 1020;
    private Uri imageUri;
    private EditText productName_txt, productDescription_txt, productPrice_txt, productSite_txt;
    String userId;
    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    ImageButton pickPlace;
    CircleImageView productImage;
    TextView locations;
    String productName, productDescription, productPrice, productSite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_items);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        userId = fireAuth.getCurrentUser().getUid();


        //addRecordBtn =findViewById(R.id.upload);
        // mapSelector =findViewById(R.id.Change);
        productName_txt = findViewById(R.id.productName);
        productDescription_txt = findViewById(R.id.productDescription);
        productSite_txt = findViewById(R.id.productSite);
        productPrice_txt = findViewById(R.id.productPrice);
        productImage = findViewById(R.id.productImage);


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
                productinformation.put("image_url", imageUri.toString());


                product.set(productinformation).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        //  Confirm success
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
                    //Failure listener to handle errors
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


    //Confirm if image has been selected in order to upload to firebase.
    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
            uploadPicture();
        }
        //Open place picker to fetch location specified by user.
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (requestCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(data, this);
                StringBuilder stringBuilder = new StringBuilder();
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

    //Upload image and fetch string url of image.
    private String uploadPicture() {

        final ProgressDialog pd = new ProgressDialog(MapsActivity.this);
        pd.setTitle("UploadingImage...");
        pd.show();

        final String randomId = UUID.randomUUID().toString();

        StorageReference productImages = mStorageRef.child("productImages/" + randomId);

        productImages.putFile(imageUri)
                .addOnCompleteListener(task -> productImages.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Uri imageDownloadUrl = uri;
                        ;
                        String url = imageDownloadUrl.toString();

                        pd.dismiss();
                        Toast.makeText(MapsActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();


                    }
                })).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
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
        ;


        //upload image to storage and save product to FireStore.
        productImages.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    pd.dismiss();
                    Toast.makeText(MapsActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();

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

        return null;

    }


}