package com.example.do_i_need_it;
/**
 * The java class Main Activity Extends AppCompatActivity
 * This class is an onboard screen that takes the user through the functionalities of the Do I need It App
 * Note application runs on a Nexus 5X API 30
 *
 * @author Thabang Fenge Isaka
 * @version 1.0
 * @since 2020-11-16
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //Declarations of components to be used
    Button button;
    TextView txt;
    FirebaseAuth fireAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = (TextView) findViewById(R.id.loginLink);
        button = (Button) findViewById(R.id.nextbtn);


        //Check if user has already logged in and redirect user to dashboard.
        fireAuth = FirebaseAuth.getInstance();
        if (fireAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity2.class));
            finish();
        }

        //onClickListner to take user to the Login Activity.
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });

        //onClickListner to take user to the next onboarding screen.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOnBoardScreenActivity();
            }
        });

    }

    public void goToOnBoardScreenActivity() {
        Intent intent = new Intent(this, onBoardScreenTwo.class);
        startActivity(intent);
    }

    public void goToLoginActivity() {
        Intent intent = new Intent(this, UserLogin.class);
        startActivity(intent);
    }
}