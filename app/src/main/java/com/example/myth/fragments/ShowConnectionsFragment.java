package com.example.myth.fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

public class ShowConnectionsFragment extends DialogFragment implements RecyclerViewInterface {

    private PreferenceManager preferenceManager;
    private RecyclerView usersRecyclerView;

    private List<User> users = new ArrayList<>();

    public ShowConnectionsFragment() {
        // Required empty public constructor
    }

    public interface OnInputSelected{
        void sendInput(User userClicked);
    }
    public OnInputSelected onInputSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pop_up_connections, container);
        initWidgets(rootView);
        getUsers();

        this.getDialog().setTitle("Dialog ");

        return rootView;
    }

    private void initWidgets(View rootView) {
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        usersRecyclerView = rootView.findViewById(R.id.connectionListRecyclerView);
    }

    private void getUsers() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_COLLECTION_CONNECTION).get()
                .addOnCompleteListener(task -> {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        users.clear();
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
                            UsersAdapter usersAdapter = new UsersAdapter(users, currentUserId, true, this);
                            usersRecyclerView.setAdapter(usersAdapter);
                            usersRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        User userSelected = users.get(position);
        onInputSelected.sendInput(userSelected);
        dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            onInputSelected = (OnInputSelected) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: " + e.getMessage());
        }
    }
}