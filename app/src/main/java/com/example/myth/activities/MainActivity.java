package com.example.myth.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.myth.fragments.CalendarFragment;
import com.example.myth.fragments.EventRequestsFragment;
import com.example.myth.fragments.NotificationsFragment;
import com.example.myth.R;
import com.example.myth.fragments.TodaysEventsFragment;
import com.example.myth.fragments.UserProfileFragment;
import com.example.myth.fragments.UsersListFragment;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navigationView;
    private PreferenceManager preferenceManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(getApplicationContext());
        getToken();
        navigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new TodaysEventsFragment()).commit();
        navigationView.setSelectedItemId(R.id.bottom_meeting);
        navigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch(item.getItemId()){
                    case R.id.bottom_calendar:
                        fragment = new CalendarFragment();
                        break;

                    case R.id.bottom_chat:
                        fragment = new EventRequestsFragment();
                        break;

                    case R.id.bottom_meeting:
                        fragment = new TodaysEventsFragment();
                        break;

                    case R.id.bottom_settings:
                        fragment = new UserProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();
                return true;
            }
        });

    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN, token);
    }
}