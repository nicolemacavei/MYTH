package com.example.myth.adapters;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myth.Connection;
import com.example.myth.Notification;
import com.example.myth.User;

import com.example.myth.databinding.ItemContainerUserBinding;
import com.example.myth.firebase.FCMSend;
import com.example.myth.interfaces.RecyclerViewInterface;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private final RecyclerViewInterface recyclerViewInterface;
    private final List<User> users;
    private final boolean connected;
    private PreferenceManager preferenceManager;

    public UsersAdapter(List<User> users, boolean connected, RecyclerViewInterface recyclerViewInterface) {
        this.users = users;
        this.connected = connected;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        preferenceManager = new PreferenceManager(parent.getContext());

        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(User user){
            binding.textName.setText(user.getFullName());
            binding.textEmail.setText(user.getEmail());
            if(user.getImage() != null) {
                binding.imageProfile.setImageBitmap(getUserImage(user.getImage()));
            }
            if(connected) {
                binding.addUserBtn.setVisibility(View.GONE);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(recyclerViewInterface != null){
                            int position = getAdapterPosition();
                            if(position != RecyclerView.NO_POSITION){
                                recyclerViewInterface.onItemClick(position);
                            }
                        }
                    }
                });
            }
            binding.addUserBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore database = FirebaseFirestore.getInstance();

                    Notification notification = new Notification(LocalDate.now().toString(),
                            preferenceManager.getString(Constants.KEY_USER_ID), null);
                    database.collection(Constants.KEY_COLLECTION_NOTIFICATION).document(user.getUserId())
                            .collection(Constants.KEY_COLLECTION_REQUEST).document(preferenceManager.getString(Constants.KEY_USER_ID)).set(notification);

                    FCMSend.pushNotification(
                            itemView.getContext(),
                            user.token,
                            "New Request",
                            "Connection request from " + preferenceManager.getString(Constants.KEY_NAME)
                    );
                    Toast.makeText(binding.getRoot().getContext(), "request sent", Toast.LENGTH_SHORT).show();
                    binding.addUserBtn.setVisibility(View.GONE);
                }
            });
        }
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
    }
}
