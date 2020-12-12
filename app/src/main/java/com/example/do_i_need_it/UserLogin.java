package com.example.do_i_need_it;
/**
 * The java class UserLogin Extends AppCompatActivity
 * This class authenticates a user using FirebaseAuth
 * Note application runs on a Nexus 5X API 30
 *
 * @author Thabang Fenge Isaka
 * @version 1.0
 * @since 2020-11-16
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

import android.Manifest;

public class UserLogin extends AppCompatActivity {


    //Variable Declarations
    EditText emailTxt,passwordTxt;
    String email,password;
    FirebaseAuth fireAuth;
    ProgressBar progressBar;
    TextView txt;
    Button loginbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        checkLocationPermission();

        //User Login Components or fields

        emailTxt =(EditText) findViewById(R.id.loguseremail);
        passwordTxt =(EditText) findViewById(R.id.loguserpassword);
        progressBar = findViewById(R.id.progressBar2);
        txt = (TextView) findViewById(R.id.toregisterlink);
        loginbtn = findViewById(R.id.loginbtn);


        //Instantiate Database and authentication Services
        fireAuth = FirebaseAuth.getInstance();



        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Fetch input data from Edit text views
                email = emailTxt.getText().toString().trim();
                password = passwordTxt.getText().toString().trim();


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
                fireAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(UserLogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            FirebaseUser user = fireAuth.getCurrentUser();


                            finish();
                            goToDashboard();

                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(UserLogin.this, "Sign in failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity();
            }
        });

//Android location permission request
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


                new AlertDialog.Builder(this)
                        .setTitle("Permission Request")
                        .setMessage("This App requires your location to work. Activate Your Location please.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ActivityCompat.requestPermissions(UserLogin.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {

                    Toast.makeText(this, "Permission", Toast.LENGTH_SHORT).show();

                }
                return;
            }

        }
    }


    public void goToRegisterActivity() {
        Intent intent = new Intent(this, UserRegisteration.class);
        startActivity(intent);
    }
    public void goToDashboard() {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }
}