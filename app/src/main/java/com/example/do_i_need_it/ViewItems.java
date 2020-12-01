package com.example.do_i_need_it;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewItems extends Fragment  {


    private Button addRecordBtn;
    String userId;
    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    FloatingActionButton popupBtn;
    ImageButton pickPlace;
    CircleImageView productImage;
    private EditText productName_txt, productDescription_txt, productPrice_txt, productSite_txt;
    String productName, productDescription, productPrice, productSite;
    public static final int REQUEST_CODE = 1;
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    private static final int ADDRESS_PICKER_REQUEST = 1020;

    private Uri imageUri;
    private ViewPager viewPager;
    private ArrayList<MyModel> modelArrayList;
    private MyAdapter myAdapter;

    MapView mapView;
    GoogleMap map;


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


        mapView = dialog.findViewById(R.id.mapp);
        mapView.onCreate(savedInstanceState);
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

                Intent intent = new Intent (getContext(),MapsActivity.class);
                startActivity(intent);

    /*
                // Display the dialog
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
*/

            }

        });








        loadCards();


        return view1;
    }


    private void loadCards() {


        modelArrayList = new ArrayList<>();

        modelArrayList.add(new MyModel(
                "Nike Shoes",
                "\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia n",
                "03/08/2020",
                R.drawable.one
        ));
        modelArrayList.add(new MyModel("Nike Shoes 2", "\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia n", "03/08/2020", R.drawable.two));
        modelArrayList.add(new MyModel("Nike Shoes 4", "\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia n", "03/08/2020", R.drawable.three));

        myAdapter = new MyAdapter(getContext(), modelArrayList);
        viewPager.setAdapter(myAdapter);
        viewPager.setPadding(100, 0, 100, 0);

    }



}
