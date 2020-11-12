package com.example.do_i_need_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class onBoardScreenTwo extends AppCompatActivity {
Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board_screen_two);

        button = (Button) findViewById(R.id.nextButton2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOnBoardScreenActivity();
            }
        });
    }

    public void goToOnBoardScreenActivity() {
        Intent intent = new Intent(this, OnBoardScreenThree.class);
        startActivity(intent);
    }
}