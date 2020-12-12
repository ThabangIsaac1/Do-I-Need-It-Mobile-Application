package com.example.do_i_need_it;

/**
 * The java class UserRegisteration Extends AppCompatActivity
 * This class regsiters a user using FirebaseAuth
 * Note application runs on a Nexus 5X API 30
 *
 * @author Thabang Fenge Isaka
 * @version 1.0
 * @since 2020-11-16
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserRegisteration extends AppCompatActivity {

    //Variable Declarations
    public static final String TAG = "TAG";
    TextView txt;
    EditText usernameTxt,emailTxt,passwordTxt;
    String username,email,password;
    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    ProgressBar progressBar;
    Button registerBtn;
    String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registeration);

        //User Registration Components or fields
        txt = (TextView) findViewById(R.id.toLoginclick);
        usernameTxt =(EditText) findViewById(R.id.regusername);
        emailTxt =(EditText) findViewById(R.id.reguseremail);
        passwordTxt =(EditText) findViewById(R.id.reguserpassword);
        registerBtn =(Button) findViewById(R.id.registerbtn);
        progressBar = findViewById(R.id.progressBar);


        //Instantiate Database and authentication Services
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        //On click listener to redirect user to login screen
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToLoginActivity();
            }
        });


        //On click listener to register user to
        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Fetch input data from Edit text views
                username = usernameTxt.getText().toString().trim();
                email = emailTxt.getText().toString().trim();
                password = passwordTxt.getText().toString().trim();


                // Form Validation
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Enter Username!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Create and Store user authentication credentials

                fireAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                        Toast.makeText(getApplicationContext(), "Account successfully created.", Toast.LENGTH_SHORT).show();

                        // Store user details:
                        userId = fireAuth.getCurrentUser().getUid();


                        DocumentReference userInformation = fireStore.collection("user").document(userId);

                        Map<String, Object> user = new HashMap<>();
                        user.put("user_name", username);


                        userInformation.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "user made ", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "User details successfully stored.");
                                System.out.println("This was accessed");
                            }
                        });
                        startActivity(new Intent(getApplicationContext(), UserLogin.class));


                    }else {
                            Toast.makeText(getApplicationContext(), "Error! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                }
                });

            }
        });


    }

    public void goToLoginActivity() {
        Intent intent = new Intent(this, UserLogin.class);
        startActivity(intent);
    }
}