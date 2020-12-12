package com.example.do_i_need_it;
/**
 * The java class onBoardScreenTwo Extends AppCompatActivity
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