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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class HomeFragment extends Fragment {

    private static final Object TAG = null;
    TextView usernameView;
    ImageView logout;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore mFirestore;
    String UserId;
    TextView numberOfProducts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_dashboard,container,false);

        //Firebase Instance
        firebaseAuth = FirebaseAuth.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        usernameView = view1.findViewById(R.id.displayusername);

        //Components Instances
        logout = view1.findViewById(R.id.logoutAction);
        numberOfProducts = view1.findViewById(R.id.addedTxtView);

        if(UserId == null){
            Intent intent = new Intent (HomeFragment.this.getActivity(),UserLogin.class);
            startActivity(intent);
        }

        mFirestore.collection("user").document(UserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String user_name = documentSnapshot.getString("user_name");
                usernameView.setText(user_name);
            }
        });





        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent (HomeFragment.this.getActivity(),UserLogin.class);
                startActivity(intent);

            }
        });
        return  view1;

    }
}


