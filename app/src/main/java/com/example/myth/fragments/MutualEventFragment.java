package com.example.myth.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myth.R;
import com.example.myth.User;
import com.example.myth.interfaces.RecyclerViewInterface;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.FirebaseFirestore;

public class MutualEventFragment extends Fragment implements ShowConnectionsFragment.OnInputSelected{

    EditText eventName, details;
    Button chooseConnectionBtn, suggestTimingBtn;
    private Slider eventDuration;
    FirebaseFirestore firebaseFirestore;
    TextView userSelectedTextView;
    Bundle bundle;

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

        chooseConnectionBtn.setOnClickListener(new View.OnClickListener() {
            final FragmentManager fragmentManager = getFragmentManager();
            final ShowConnectionsFragment connectionsFragment = new ShowConnectionsFragment();
            @Override
            public void onClick(View v) {
                connectionsFragment.setTargetFragment(MutualEventFragment.this, 1);
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
        userSelectedTextView = rootView.findViewById(R.id.userSelectedText);
    }

    @Override
    public void sendInput(User userSelected) {
        Log.e(TAG, "sendInput: " + userSelected.getFullName());
        chooseConnectionBtn.setVisibility(View.GONE);
        userSelectedTextView.setText(userSelected.getFullName());
        userSelectedTextView.setVisibility(View.VISIBLE);
    }
}