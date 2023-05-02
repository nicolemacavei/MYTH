package com.example.myth.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myth.R;

import org.w3c.dom.Text;

public class MutualEventFinalStepFragment extends Fragment {

    private TextView userSelectedText, suggestedDateText, suggestedTimeText;

    private EditText nameEventEditText, detailsEventEditText;

    public MutualEventFinalStepFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_mutual_event_final_step, container, false);
        initWidgets(rootView);

        return rootView;
    }

    private void initWidgets(View rootView) {
        suggestedDateText = rootView.findViewById(R.id.suggestedDateText);
        suggestedTimeText = rootView.findViewById(R.id.suggestedTimeText);
        nameEventEditText = rootView.findViewById(R.id.nameEventEditText);
        detailsEventEditText = rootView.findViewById(R.id.detailsEventEditText);
        userSelectedText = rootView.findViewById(R.id.userSelectedText);

    }
}