package com.example.myth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.Inflater;

public class UserProfileFragment extends Fragment {

    FirebaseAuth auth;
    Button logoutBtn;
    TextView userEmail,userName;
    FirebaseUser user;
    ImageView profileImage;
    private final int PICK_IMAGE = 100;

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

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse("C:\\Users\\macav\\AndroidStudioProjects\\Myth\\app\\src\\main\\res\\drawable\\myth_icon.png")).build();
                user.updateProfile(profileUpdates);
//                profileImage.setImageBitmap(getBitmapFromURL(user.getPhotoUrl().toString()));

            }
        });

        return rootView;
    }

    private static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getUserDetails() {
        userEmail.setText(user.getEmail());
        userName.setText(user.getDisplayName());
    }

    private void initWidgets(View rootView) {
        auth = FirebaseAuth.getInstance();
        logoutBtn = rootView.findViewById(R.id.logoutBtn);
        userEmail = rootView.findViewById(R.id.emailUserProfile);
        userName = rootView.findViewById(R.id.userNameProfile);
        profileImage = rootView.findViewById(R.id.userProfileImage);
        user = auth.getCurrentUser();
    }

}