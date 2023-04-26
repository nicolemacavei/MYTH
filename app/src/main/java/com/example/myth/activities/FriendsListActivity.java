package com.example.myth.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.myth.R;
import com.example.myth.User;
import com.example.myth.adapters.UsersAdapter;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private RecyclerView usersRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        initWidgets();
        getUsers();
    }

    private void initWidgets() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        usersRecyclerView = findViewById(R.id.friendsRecyclerView);
    }

    private void getUsers() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_COLLECTION_CONNECTION).get()
                .addOnCompleteListener(task -> {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<User> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){

                            String userId = queryDocumentSnapshot.getString(Constants.KEY_USER_ID);
                            String email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            String name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            String image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            String token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            User user = new User(
                                    userId,
                                    email,
                                    name,
                                    image,
                                    token
                            );
                            users.add(user);
                        }
                        if(users.size() > 0){
                            UsersAdapter usersAdapter = new UsersAdapter(users, currentUserId);
                            usersRecyclerView.setAdapter(usersAdapter);
                            usersRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}