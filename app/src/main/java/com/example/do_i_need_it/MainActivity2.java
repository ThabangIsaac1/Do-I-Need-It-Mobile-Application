package com.example.do_i_need_it;

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

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        ((FragmentTransaction) tx).replace(R.id.fragment_container, new HomeFragment());
        tx.commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bttm_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

   BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new
           BottomNavigationView.OnNavigationItemSelectedListener() {
               @Override
               public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                   Fragment selectedFragment = null;

                   switch (item.getItemId()){

                       case R.id.nav_home:
                           selectedFragment = new HomeFragment();
                       break;
                       case R.id.nav_profile:
                           selectedFragment = new ProfileFragment();
                           break;
                       case R.id.nav_add:
                           selectedFragment = new ProfileFragment();
                           break;
                   }
                   getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                   return  true;


               }
           };
}