package com.example.myth.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.myth.R;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.FirebaseFirestore;

public class MutualEventFragment extends Fragment {

    EditText eventName, details;
    Button chooseConnectionBtn, suggestTimingBtn;
    private Slider eventDuration;
    FirebaseFirestore firebaseFirestore;

    public MutualEventFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_mutual_event, container, false);
        initWidgets(rootView);

        final FragmentManager fragmentManager = getFragmentManager();
        final ShowConnectionsFragment connectionsFragment = new ShowConnectionsFragment();
        chooseConnectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionsFragment.show(fragmentManager, "TV_tag");
            }
        });

        return rootView;
    }

    private void initWidgets(View rootView) {
        eventName = rootView.findViewById(R.id.nameEventEditText);
        details = rootView.findViewById(R.id.detailsEventEditText);
        eventDuration = rootView.findViewById(R.id.durationSlideBar);
        chooseConnectionBtn = rootView.findViewById(R.id.chooseConnectionBtn);
        suggestTimingBtn = rootView.findViewById(R.id.suggestTimingBtn);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
}