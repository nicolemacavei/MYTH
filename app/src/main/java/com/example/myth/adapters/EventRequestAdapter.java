package com.example.myth.adapters;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myth.Event;
import com.example.myth.databinding.ItemContainerEventRequestBinding;
import com.example.myth.findTimeSlots.TimeSlot;
import com.example.myth.firebase.FCMSend;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

public class EventRequestAdapter extends RecyclerView.Adapter<EventRequestAdapter.EventRequestViewHolder> {

    private final List<Event> events;
    private final String userName, userId;
    private PreferenceManager preferenceManager;

    public EventRequestAdapter(List<Event> events, String userName, String userId) {
        this.events = events;
        this.userName = userName;
        this.userId = userId;
    }

    @NonNull
    @Override
    public EventRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerEventRequestBinding itemContainerEventRequestBinding = ItemContainerEventRequestBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        preferenceManager = new PreferenceManager(parent.getContext());
        return new EventRequestViewHolder(itemContainerEventRequestBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventRequestViewHolder holder, int position) {
        holder.setEventRequestData(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventRequestViewHolder extends RecyclerView.ViewHolder{

        ItemContainerEventRequestBinding binding;
        public EventRequestViewHolder(@NonNull ItemContainerEventRequestBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        void setEventRequestData(Event event){
            binding.textName.setText(userName);
            binding.eventTime.setText(event.toString());
            binding.eventName.setText(event.getName());
            binding.eventDate.setText(event.getDate());
            binding.acceptEventBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore database = FirebaseFirestore.getInstance();

                    database.collection(Constants.KEY_COLLECTION_USERS).document(userId)
                            .get().addOnCompleteListener(task -> {

                                if(task.isSuccessful() && task.getResult() != null) {

                                    String userToken = task.getResult().getString(Constants.KEY_FCM_TOKEN);

                                    database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID))
                                            .collection(Constants.KEY_COLLECTION_DATE).document(event.getDate())
                                            .collection(Constants.KEY_COLLECTION_EVENT).document(event.getEventId()).set(event);
                                    database.collection(Constants.KEY_COLLECTION_USERS).document(userId)
                                            .collection(Constants.KEY_COLLECTION_DATE).document(event.getDate())
                                            .collection(Constants.KEY_COLLECTION_EVENT).document(event.getEventId()).set(event);

                                    FCMSend.pushNotification(
                                            itemView.getContext(),
                                            userToken,
                                            "Event on " + event.getDate() + " accepted",
                                            "by " + preferenceManager.getString(Constants.KEY_NAME)
                                    );

                                    database.collection(Constants.KEY_COLLECTION_NOTIFICATION).document(preferenceManager.getString(Constants.KEY_USER_ID))
                                            .collection(Constants.KEY_COLLECTION_EVENT).document(event.getEventId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()) {
                                                        Toast.makeText(binding.getRoot().getContext(), "event accepted", Toast.LENGTH_SHORT).show();
                                                        binding.acceptEventBtn.setVisibility(View.GONE);
                                                    }
                                                    else
                                                        Log.e(TAG, "delete failed: " + task.getException());
                                                }
                                            });
                                }
                            });
                }
            });

        }
    }
}
