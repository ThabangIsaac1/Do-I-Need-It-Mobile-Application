package com.example.do_i_need_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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