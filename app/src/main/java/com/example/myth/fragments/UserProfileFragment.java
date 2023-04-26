package com.example.myth.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myth.R;
import com.example.myth.activities.FriendsListActivity;
import com.example.myth.activities.LoginActivity;
import com.example.myth.activities.MainActivity;
import com.example.myth.activities.NewEventActivity;
import com.example.myth.activities.ResetPasswordActivity;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class UserProfileFragment extends Fragment {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userId = auth.getCurrentUser().getUid();
    private Button logoutBtn, deleteAccountBtn, changePassword;
    private TextView userEmail,userName, friendsList;
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

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });

        friendsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendsListActivity.class);
                getActivity().startActivity(intent);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = preferenceManager.getString(Constants.KEY_EMAIL);
                auth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "check your email inbox", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                        .document(preferenceManager.getString(Constants.KEY_USER_ID));
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
            }
        });

        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder deleteAccountDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Delete MYTH account")
                        .setMessage("Please enter your password: ");

                final EditText passwordInput = new EditText(getActivity());
                passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

                deleteAccountDialog.setView(passwordInput).setPositiveButton("delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String password = passwordInput.getText().toString();
                                deleteAccount(password);
                            }
                        })
                        .setNegativeButton("cancel", null).show();

            }
        });

        return rootView;
    }

    private void deleteAccount(String password) {

        if(user != null) {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(preferenceManager.getString(Constants.KEY_EMAIL), password);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                                .document(preferenceManager.getString(Constants.KEY_USER_ID)).delete()
                                .addOnSuccessListener(unused -> {
                                    preferenceManager.clear();
                                    user.delete();
                                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                });
                    } else {
                        Toast.makeText(getActivity(), "wrong password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
        friendsList = rootView.findViewById(R.id.closeFriendsUserProfile);
        user = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        deleteAccountBtn = rootView.findViewById(R.id.deletUserBtn);
        changePassword = rootView.findViewById(R.id.changePasswordBtn);
    }

}