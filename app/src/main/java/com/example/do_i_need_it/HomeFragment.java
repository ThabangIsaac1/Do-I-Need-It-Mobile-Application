package com.example.do_i_need_it;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class HomeFragment extends Fragment {

    TextView usernameView;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore mFirestore;
    String UserId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_dashboard,container,false);


        firebaseAuth = FirebaseAuth.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        usernameView = (TextView) view1.findViewById(R.id.displayusername);

        mFirestore.collection("user").document(UserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String user_name = documentSnapshot.getString("user_name");
                usernameView.setText(user_name);


            }
        });
        return  view1;

    }
}


