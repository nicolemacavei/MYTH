package com.example.myth.fragments;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myth.R;
import com.example.myth.User;
import com.example.myth.adapters.NotificationAdapter;
import com.example.myth.adapters.UsersAdapter;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private PreferenceManager preferenceManager;
    private RecyclerView notificationRecyclerView;
    private Button addConnectionBtn;

    public NotificationsFragment() {
        // Required empty public constructor
    }

//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifications_list, container, false);
        initWidgets(rootView);
        getNotifications();

        addConnectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.body_container, new UsersListFragment() );
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return rootView;
    }

    private void initWidgets(View rootView){
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        notificationRecyclerView = rootView.findViewById(R.id.notificationRecyclerView);
        addConnectionBtn = rootView.findViewById(R.id.addConnectionBtn);
    }

    private void getNotifications() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        database.collection(Constants.KEY_COLLECTION_USERS).document(currentUserId)
                .collection(Constants.KEY_COLLECTION_REQUEST).get()
                .addOnCompleteListener(task -> {

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
                            NotificationAdapter notificationAdapter = new NotificationAdapter(users, currentUserId);
                            notificationRecyclerView.setAdapter(notificationAdapter);
                            notificationRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

}