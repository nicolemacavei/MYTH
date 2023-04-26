package com.example.myth.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myth.R;
import com.example.myth.User;
import com.example.myth.adapters.UsersAdapter;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersListFragment extends Fragment {

    private PreferenceManager preferenceManager;
    private RecyclerView usersRecyclerView;
//    private Button addUserBtn;

    public UsersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_users_list, container, false);
        initWidgets(rootView);
        getUsers();

        return rootView;
    }

    private void initWidgets(View rootView) {
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        usersRecyclerView = rootView.findViewById(R.id.usersRecyclerView);
//        addUserBtn = rootView.findViewById(R.id.addUserBtn);
    }

    private void getUsers(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS).get()
                .addOnCompleteListener(task -> {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<User> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if(currentUserId.equals(queryDocumentSnapshot.getId()))
                                continue;

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