package com.example.myth.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myth.Connection;
import com.example.myth.User;
import com.example.myth.databinding.ItemContainerNotificationBinding;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<User> users;
    private final String currentUID;
    private PreferenceManager preferenceManager;

    public NotificationAdapter(List<User> users, String currentUID) {
        this.users = users;
        this.currentUID = currentUID;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerNotificationBinding itemContainerNotificationBinding = ItemContainerNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        preferenceManager = new PreferenceManager(parent.getContext());
        return new NotificationViewHolder(itemContainerNotificationBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.setNotificationData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder{

        ItemContainerNotificationBinding binding;

        public NotificationViewHolder(@NonNull ItemContainerNotificationBinding itemView) {

            super(itemView.getRoot());
            binding = itemView;
        }

        void setNotificationData(User user){
            binding.textName.setText(user.getFullName());
            binding.textEmail.setText(user.getEmail());
            if(user.getImage() != null) {
                binding.imageProfile.setImageBitmap(getUserImage(user.getImage()));
            }
            binding.rejectUserBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    //delete request from DB
                    database.collection(Constants.KEY_COLLECTION_NOTIFICATION).document(currentUID)
                            .collection(Constants.KEY_COLLECTION_REQUEST).document(user.getUserId()).delete();

                    Toast.makeText(binding.getRoot().getContext(), "request rejected", Toast.LENGTH_SHORT).show();
                    binding.acceptUserBtn.setVisibility(View.GONE);
                    binding.rejectUserBtn.setVisibility(View.GONE);
                }
            });
            binding.acceptUserBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore database = FirebaseFirestore.getInstance();

                    Connection newConnection = new Connection(user.userId);
                    //add connection to DB to both users
                    database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID))
                            .collection(Constants.KEY_COLLECTION_CONNECTION).document(user.userId).set(newConnection);

                    newConnection = new Connection(currentUID);
                    database.collection(Constants.KEY_COLLECTION_USERS).document(user.getUserId())
                            .collection(Constants.KEY_COLLECTION_CONNECTION).document(currentUID).set(newConnection);

                    //delete Request from DB
                    database.collection(Constants.KEY_COLLECTION_NOTIFICATION).document(currentUID)
                            .collection(Constants.KEY_COLLECTION_REQUEST).document(user.getUserId()).delete();
                    Toast.makeText(binding.getRoot().getContext(), "request accepted", Toast.LENGTH_SHORT).show();
                    binding.acceptUserBtn.setVisibility(View.GONE);
                    binding.rejectUserBtn.setVisibility(View.GONE);
                }
            });
        }
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
    }
}
