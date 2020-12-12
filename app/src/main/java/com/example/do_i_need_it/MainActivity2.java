package com.example.do_i_need_it;
/**
 * The java class Main Activity2 Extends AppCompatActivity
 * This class is also an is the controller of user navigation that takes place in the bottom menu action.
 * Note application runs on a Nexus 5X API 30
 *
 * @author Thabang Fenge Isaka
 * @version 1.0
 * @since 2020-11-16
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Create instance of fragment manager and replace the fragment container with the dashboard screen
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        ((FragmentTransaction) tx).replace(R.id.fragment_container, new HomeFragment());
        tx.commit();

        //Instantiate the bottom navigation menu and create an OnNavigationItemSelectedListener to select pages accordingly.
        BottomNavigationView bottomNavigationView = findViewById(R.id.bttm_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    //OnNavigationItemSelectedListener followed by a switch case to display selected pages
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {

                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                        case R.id.nav_add:
                            selectedFragment = new ViewItems();
                            break;


                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;


                }
            };
}