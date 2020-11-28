package com.example.do_i_need_it;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class AddItems extends Fragment  {



    private Button addRecordBtn;
    String userId;
    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    FloatingActionButton popupBtn;
    ImageButton pickPlace;
    CircleImageView productImage;
    private EditText productName_txt, productDescription_txt,productPrice_txt,productSite_txt;
    String productName,productDescription,productPrice,productSite;
    public static final int REQUEST_CODE = 1;
    private StorageReference mStorageRef;
    private  FirebaseStorage storage;
    private static final int ADDRESS_PICKER_REQUEST = 1020;

    private  Uri imageUri;
    private ViewPager viewPager;
    private ArrayList <MyModel> modelArrayList;
    private MyAdapter myAdapter;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_add_items,container,false);





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



        addRecordBtn = dialog.findViewById(R.id.upload);
        productName_txt = dialog.findViewById(R.id.productName);
        productDescription_txt = dialog.findViewById(R.id.productDescription);
        productSite_txt = dialog.findViewById(R.id.productSite);
        productPrice_txt = dialog.findViewById(R.id.productPrice);
        productImage = dialog.findViewById(R.id.productImage);



            //Open add product Dialog
        popupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                // Display the dialog
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);

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
                    Toast.makeText(getContext(), "Enter Product Name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(productDescription)) {
                    Toast.makeText(getContext(), "Enter Product Description!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(productSite)) {
                    Toast.makeText(getContext(), "Enter Product Website Url!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(productPrice)) {
                    Toast.makeText(getContext(), "Enter Product Price!", Toast.LENGTH_SHORT).show();
                    return;
                }


                //Add product document to firestore for user
                userId = fireAuth.getCurrentUser().getUid();
                DocumentReference product = fireStore.collection("products").document(userId);

                Map<String, Object> productinformation = new HashMap<>();
                productinformation.put("product_name", productName);
                productinformation.put("product_description", productDescription);
                productinformation.put("product_site", productSite);
                productinformation.put("product_price", productPrice);



                product.set(productinformation).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Product Added Successfully ", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, "Product details successfully stored.");
                        System.out.println("This was accessed");
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Product Failed To add ", Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });






        loadCards();



     return  view1;
    }



    private void loadCards() {


            modelArrayList = new ArrayList<>();

            modelArrayList.add(new MyModel(
                    "Nike Shoes",
                    "\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia n",
                    "03/08/2020",
                     R.drawable.one
            ));
modelArrayList.add(new MyModel("Nike Shoes 2","\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia n","03/08/2020",R.drawable.two));
        modelArrayList.add(new MyModel("Nike Shoes 4","\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia n","03/08/2020",R.drawable.three));

        myAdapter = new MyAdapter(getContext(), modelArrayList);
        viewPager.setAdapter(myAdapter);
        viewPager.setPadding(100,0,100,0);

    }


//Select Image
    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
             imageUri = data.getData();
             productImage.setImageURI(imageUri);
             uploadPicture();



        }
    }



    private void uploadPicture() {

                final ProgressDialog pd = new ProgressDialog(getContext());
                pd.setTitle("UploadingImage...");
                pd.show();

        userId = fireAuth.getCurrentUser().getUid();

        StorageReference productImages = mStorageRef.child("productImages/" + userId.toString());

        productImages.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getContext(), "image Failed To Upload", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
               double progressPercent = (100.00 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
               pd.setMessage("Percentage: " + (int) progressPercent + "%");


            }
        });


    }

}
