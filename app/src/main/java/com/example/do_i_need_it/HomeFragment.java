/**
 * The java class Home fragment Extends fragment
 * Displays dashboard statistics and analytics
 * Note application runs on a Nexus 5X API 30
 *
 * @author Thabang Fenge Isaka
 * @version 1.0
 * @since 2020-11-16
 */


package com.example.do_i_need_it;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {


    //Decelerations of variables and components to be used in the class
    private static final Object TAG = null;
    TextView usernameView;
    ImageView logout;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore mFirestore;
    String UserId;
    TextView numberOfProducts, numberOfdeletedProducts, purchasedItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_dashboard, container, false);

        //Firebase Instance
        firebaseAuth = FirebaseAuth.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        usernameView = view1.findViewById(R.id.displayusername);

        //Components Instances
        logout = view1.findViewById(R.id.logoutAction);
        numberOfProducts = view1.findViewById(R.id.addedTxtView);
        numberOfdeletedProducts = view1.findViewById(R.id.deletedTxtView);
        purchasedItems = view1.findViewById(R.id.item_purchased);


        //Check If User is logged In
        if (UserId == null) {
            Intent intent = new Intent(HomeFragment.this.getActivity(), UserLogin.class);
            startActivity(intent);
        }


        //Fetch Deleted Products according to product owner from database
        mFirestore.collection("deleted_products").whereEqualTo("owner", firebaseAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                count++;
                                System.out.println("The size" + count);
                                String total = String.valueOf(count);
                                numberOfdeletedProducts.setText(total);
                            }
                        } else {
                            Log.d(String.valueOf(TAG), "Error getting documents: ", task.getException());
                        }
                    }
                });


        //Fetch Purchased Products for the user currently logged in
        mFirestore.collection("purchasedItems").whereEqualTo("owner", firebaseAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                count++;
                                System.out.println("The size" + count);
                                String total = String.valueOf(count);
                                purchasedItems.setText(total);
                            }
                        } else {
                            Log.d(String.valueOf(TAG), "Error getting documents: ", task.getException());
                        }
                    }
                });


        //Fetch users from database and display their user name in a textview
        mFirestore.collection("user").document(UserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String user_name = documentSnapshot.getString("user_name");
                usernameView.setText(user_name);
            }
        });


        //Log user out from system
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeFragment.this.getActivity(), UserLogin.class);
                startActivity(intent);

            }
        });


        //Fetch the number of product items
        //Load array and get firestore instance.
        ArrayList<MyModel> modelArrayList;
        modelArrayList = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();

        Query query = mFirestore.collection("products").whereEqualTo("product_owner", firebaseAuth.getCurrentUser().getEmail());
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        //Fetch from firebase all the documents
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
                            MyModel products = new MyModel(prodId, productName, product_description, date, product_address, owner, user_id, product_site, image_url, latitude, longitude, product_price, status);

                            //Add products to the list
                            modelArrayList.add(products);

                            //Display Number of total Products
                            String number = String.valueOf(modelArrayList.size());
                            numberOfProducts.setText(number);


                        }
                        Log.d(String.valueOf(TAG), "Array Items => " + modelArrayList.size());
                    } else {
                        Log.d(String.valueOf(TAG), "Error getting documents: ", task.getException());
                    }
                });
        return view1;
    }


}


