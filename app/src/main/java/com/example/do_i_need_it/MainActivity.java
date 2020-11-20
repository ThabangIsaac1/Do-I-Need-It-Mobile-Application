package com.example.do_i_need_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
Button button;
TextView txt;
FirebaseAuth fireAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = (TextView) findViewById(R.id.loginLink);
        button = (Button) findViewById(R.id.nextbtn);

        fireAuth = FirebaseAuth.getInstance();
        if(fireAuth.getCurrentUser() != null) {
         startActivity(new Intent(getApplicationContext(), MainActivity2.class));
        finish();
        }


        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });
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