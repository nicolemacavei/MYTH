package com.example.myth.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myth.User;

import com.example.myth.databinding.ItemContainerUserBinding;
import com.example.myth.firebase.FCMSend;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private final List<User> users;
    private final String currentUID;
    private PreferenceManager preferenceManager;

    public UsersAdapter(List<User> users, String currentUID) {
        this.users = users;
        this.currentUID = currentUID;
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
            binding.addUserBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    User currentUser = new User(
                            preferenceManager.getString(Constants.KEY_USER_ID),
                            preferenceManager.getString(Constants.KEY_EMAIL),
                            preferenceManager.getString(Constants.KEY_NAME),
                            preferenceManager.getString(Constants.KEY_IMAGE),
                            preferenceManager.getString(Constants.KEY_FCM_TOKEN)
                    );
                    database.collection(Constants.KEY_COLLECTION_USERS).document(user.getUserId())
                            .collection(Constants.KEY_COLLECTION_REQUEST).document(currentUID).set(currentUser);
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
