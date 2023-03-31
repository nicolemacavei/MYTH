package com.example.myth;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.zip.Inflater;

public class UserProfileFragment extends Fragment {

    FirebaseAuth auth;
    Button logoutBtn;
    TextView userName;
    FirebaseUser user;
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    private String mParam1;
//    private String mParam2;
//
//    public UserProfileFragment() {
//        // Required empty public constructor
//    }
//
//    public static UserProfileFragment newInstance(String param1, String param2) {
//        UserProfileFragment fragment = new UserProfileFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        initWidgets(rootView);
        getUserDetails();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return rootView;
    }

    private void getUserDetails() {
        userName.setText(user.getEmail());
    }

    private void initWidgets(View rootView) {
        auth = FirebaseAuth.getInstance();
        logoutBtn = rootView.findViewById(R.id.logoutBtn);
        userName = rootView.findViewById(R.id.userNameProfile);
        user = auth.getCurrentUser();
    }

}