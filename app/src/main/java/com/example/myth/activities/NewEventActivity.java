package com.example.myth.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myth.R;
import com.example.myth.fragments.MutualEventFragment;
import com.example.myth.fragments.SingleEventFragment;
import com.example.myth.fragments.UsersListFragment;
import com.example.myth.utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NewEventActivity extends AppCompatActivity {

    BottomNavigationView navigationView;
    private PreferenceManager preferenceManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        preferenceManager = new PreferenceManager(getApplicationContext());
        navigationView = findViewById(R.id.top_event_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.top_event_container, new SingleEventFragment()).commit();
        navigationView.setSelectedItemId(R.id.top_single_event);
        navigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch(item.getItemId()){
                    case R.id.top_single_event:
                        fragment = new SingleEventFragment();
                        break;

                    case R.id.top_mutual_event:
                        fragment = new MutualEventFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.top_event_container, fragment).commit();
                return true;
            }
        });

    }
}
