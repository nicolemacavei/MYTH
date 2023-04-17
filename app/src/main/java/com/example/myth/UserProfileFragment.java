package com.example.myth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myth.activities.LoginActivity;
import com.example.myth.activities.MainActivity;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class UserProfileFragment extends Fragment {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userId = auth.getCurrentUser().getUid();
    private Button logoutBtn;
    private TextView userEmail,userName;
    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore;
    private ImageView profileImage;
    private String encodedImage;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        initWidgets(rootView);
        getUserDetails();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                                .document( preferenceManager.getString(Constants.KEY_USER_ID));
                HashMap<String, Object> updates = new HashMap<>();
                updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
                documentReference.update(updates)
                        .addOnSuccessListener(unused -> {
                            preferenceManager.clear();
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        });
//                preferenceManager.clear();
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
//                startActivity(intent);
//                getActivity().finish();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });

        return rootView;
    }

    private void updateImageFirestore(){
        //System.out.println("IMAGE -----------  " + encodedImage);
        DocumentReference docRef = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS).document(userId);
        docRef.update(Constants.KEY_IMAGE, encodedImage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("ON SUCCESS");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("FAILED BECAUSE: " + e);
                    }
                });
        preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == MainActivity.RESULT_OK){
                    if(result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            profileImage.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                            updateImageFirestore();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void getUserDetails() {
        userEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        userName.setText(preferenceManager.getString(Constants.KEY_NAME));

        if(preferenceManager.getString(Constants.KEY_IMAGE) != null) {
            byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            profileImage.setImageBitmap(bitmap);
        }
    }

    private void initWidgets(View rootView) {
        auth = FirebaseAuth.getInstance();
        logoutBtn = rootView.findViewById(R.id.logoutBtn);
        userEmail = rootView.findViewById(R.id.emailUserProfile);
        userName = rootView.findViewById(R.id.userNameProfile);
        profileImage = rootView.findViewById(R.id.userProfileImage);
        user = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
    }

}