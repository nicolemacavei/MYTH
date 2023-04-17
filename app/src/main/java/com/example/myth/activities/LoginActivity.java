package com.example.myth.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.myth.R;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private EditText loginEmail, loginPassword;
    private TextView registerButton, resetPassword, loginButton;
    private PreferenceManager preferenceManager;
    private String image;

    @Override
    protected void onStart() {
        super.onStart();
        preferenceManager = new PreferenceManager(getApplicationContext());

        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            setSharedPrefs(auth.getCurrentUser().getUid());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.emailLoginEditText);
        loginPassword = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginBtn);
        registerButton = findViewById(R.id.registerPageBtn);
        resetPassword = findViewById(R.id.resetPasswordPageBtn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String pass = loginPassword.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if(!pass.isEmpty()){
                        auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                setSharedPrefs(auth.getCurrentUser().getUid());
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        loginPassword.setError("Password cannot be empty");
                    }
                } else if(email.isEmpty()){
                    loginEmail.setError("Email cannot be empty");
                } else {
                    loginEmail.setError("Please enter a valid email");
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
    }

    private void setSharedPrefs(String userId) {
        preferenceManager.putString(Constants.KEY_USER_ID, userId);
        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
        preferenceManager.putString(Constants.KEY_NAME, auth.getCurrentUser().getDisplayName());
        preferenceManager.putString(Constants.KEY_EMAIL, auth.getCurrentUser().getEmail());

        firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference userDetails = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS).document(userId);
        System.out.println(" TEST USER DETAILS: " + userDetails);
        userDetails.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                image = documentSnapshot.getString(Constants.KEY_IMAGE);
                System.out.println("IMAGE TEST " + image);
                preferenceManager.putString(Constants.KEY_IMAGE, image);
            }
        });

    }
}