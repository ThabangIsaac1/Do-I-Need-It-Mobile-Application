package com.example.do_i_need_it;
/**
 * The java class ProfileFragment Extends Fragment
 * It displays the user details
 * Note application runs on a Nexus 5X API 30
 *
 * @author Thabang Fenge Isaka
 * @version 1.0
 * @since 2020-11-16
 */
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.do_i_need_it.UserRegisteration.TAG;


public class ProfileFragment extends Fragment {
    CircleImageView profileImage;
    String  updateEmail,updatePassword,UpdateUsername;
   TextView updateEmailTxt,updatePasswordTxt,updateUsernameTxt;
    Button updateProfileBtb;
    FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;    FirebaseFirestore mFirestore;
    String UserId;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_profile,container,false);



        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
        UserId = firebaseAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        updateEmailTxt = view1.findViewById(R.id.profemail);
        profileImage = view1.findViewById(R.id.profile_image);
        updateUsernameTxt = view1.findViewById(R.id.profusername);
        updatePasswordTxt = view1.findViewById(R.id.profpassword);
        updateProfileBtb =view1.findViewById(R.id.profupdatebtn);

            //Check If user is logged in
        if(UserId == null){
            Intent intent = new Intent (ProfileFragment.this.getActivity(),UserLogin.class);
            startActivity(intent);
        }


        //Fetch current user details
        mFirestore.collection("user").document(UserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String user_name = documentSnapshot.getString("user_name");
                updateUsernameTxt.setText(user_name);
                updateEmailTxt.setText(user.getEmail().toString());


            }
        });



        //Update user profile
        updateProfileBtb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                updateEmail =  updateEmailTxt.getText().toString().trim();
                UpdateUsername =  updateUsernameTxt.getText().toString().trim();
                updatePassword = updatePasswordTxt.getText().toString().trim();



            }
        });








        return view1;
    }







}
