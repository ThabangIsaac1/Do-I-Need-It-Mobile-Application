package com.example.do_i_need_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserLogin extends AppCompatActivity {
    TextView txt;
    Button loginbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        txt = (TextView) findViewById(R.id.toregisterlink);
        loginbtn = findViewById(R.id.loginbtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openDashboard();
            }
        });
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity();
            }
        });


    }

    private void openDashboard() {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }

    public void goToRegisterActivity() {
        Intent intent = new Intent(this, UserRegisteration.class);
        startActivity(intent);
    }
}