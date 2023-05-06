package com.example.myth.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myth.R;
import com.example.myth.User;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

public class MutualEventRequestFragment extends Fragment {

    private TextView userSelectedText, suggestedDateText, suggestedTimeText;
    private Button sendRequestBtn;
    private EditText eventName, eventDetails;
    private FirebaseFirestore firebaseFirestore;
    private PreferenceManager preferenceManager;

    public MutualEventRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_mutual_event_request, container, false);
        initWidgets(rootView);
        getFragmentManager().setFragmentResultListener("dataFromMutualEvent", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String timeSlot = result.getString("timeSlot");
                String date = result.getString("date");
                User userName = result.getParcelable("user");
                //String userName = result.getString("userName");
                suggestedTimeText.setText(timeSlot);
                suggestedDateText.setText(date);
                userSelectedText.setText(userName.getUserId());
            }
        });
        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eventName != null){
                    //create Notification class and notification for user
                    firebaseFirestore.collection(Constants.KEY_COLLECTION_NOTIFICATION)
                            .document(preferenceManager.getString(Constants.KEY_USER_ID)).set(eventDetails);
                } else {
                    eventName.setError("Name is mandatory");
                }
            }
        });

        return rootView;
    }

    private void initWidgets(View rootView) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        suggestedDateText = rootView.findViewById(R.id.suggestedDateText);
        suggestedTimeText = rootView.findViewById(R.id.suggestedTimeText);
        eventName = rootView.findViewById(R.id.nameEventEditText);
        eventDetails = rootView.findViewById(R.id.detailsEventEditText);
        userSelectedText = rootView.findViewById(R.id.userSelectedText);
        sendRequestBtn = rootView.findViewById(R.id.sendRequestBtn);
    }
}