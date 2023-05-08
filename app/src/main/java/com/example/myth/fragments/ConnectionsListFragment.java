package com.example.myth.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myth.R;
import com.example.myth.User;
import com.example.myth.adapters.UsersAdapter;
import com.example.myth.interfaces.RecyclerViewInterface;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ConnectionsListFragment extends DialogFragment implements RecyclerViewInterface {

    private PreferenceManager preferenceManager;
    private RecyclerView usersRecyclerView;
    private List<String> userIds = new ArrayList<>();

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_friends_list);
//        initWidgets();
//        getUsers();
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_friends_list, container, false);
        initWidgets(rootView);
        getUsers();

        return rootView;
    }

    private void initWidgets(View rootView) {
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        usersRecyclerView = rootView.findViewById(R.id.friendsRecyclerView);
    }

    private void getUsers() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_COLLECTION_CONNECTION).get()
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful() && task.getResult() != null){
                        List<User> users = new ArrayList<>();
                        userIds.clear();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){

                            String userId = queryDocumentSnapshot.getString(Constants.KEY_USER_ID);
                            userIds.add(userId);

                        }
                        database.collection(Constants.KEY_COLLECTION_USERS).get()
                                .addOnCompleteListener( task1 -> {
                                    if(task.isSuccessful() && task.getResult() != null){

                                        for (QueryDocumentSnapshot queryDocumentSnapshot: task1.getResult()){
                                            String userId = queryDocumentSnapshot.getString(Constants.KEY_USER_ID);
                                            if(userIds.contains(userId)){

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
                                        }

                                    }
                                    if(users.size() > 0){
                                        UsersAdapter usersAdapter = new UsersAdapter(users,true, this);
                                        usersRecyclerView.setAdapter(usersAdapter);
                                        usersRecyclerView.setVisibility(View.VISIBLE);
                                    }

                                });
                    }
                });
    }

    @Override
    public void onItemClick(int position) {

    }
}