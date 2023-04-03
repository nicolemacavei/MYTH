package com.example.myth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private EditText signupEmail, signupPassword, signupPasswordTwo, signupLastName, signupFirstName;
    private Button signupButton;
    private TextView loginRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        signupLastName = findViewById(R.id.lastnameEditText);
        signupFirstName = findViewById(R.id.firstnameEditText);
        signupEmail = findViewById(R.id.emailRegisterEditText);
        signupPassword = findViewById(R.id.passwordEditText);
        signupPasswordTwo = findViewById(R.id.rePasswordEditText);
        signupButton = findViewById(R.id.registerBtn);
        loginRedirectText = findViewById(R.id.loginPageBtn);
        firebaseFirestore = FirebaseFirestore.getInstance();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String passTwo = signupPasswordTwo.getText().toString().trim();
                String lastName = signupLastName.getText().toString().trim();
                String firstName = signupFirstName.getText().toString().trim();
                String name = firstName + " " + lastName;

                if(email.isEmpty())
                {
                    signupEmail.setError("Email is mandatory");
                }else if(pass.isEmpty()) {
                    signupPassword.setError("Select a password");
                } else if(lastName.isEmpty() || firstName.isEmpty()){
                    signupFirstName.setError("Mandatory");
                    signupLastName.setError("Mandatory");
                }else {
                    if(!pass.equals(passTwo)){
                        Toast.makeText(RegisterActivity.this, "Passwords are not matching", Toast.LENGTH_SHORT).show();
                    } else if(!(pass.length() >= 8)){
                        Toast.makeText(RegisterActivity.this, "Password needs at least 8 characters", Toast.LENGTH_SHORT).show();
                    } else if (doesNotContainCharacters(pass)) {
                        Toast.makeText(RegisterActivity.this, "Password must contain at least one small letter and one digit", Toast.LENGTH_SHORT).show();
                    } else {
                        auth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User registeredUser = new User(
                                            email,
                                            pass,
                                            lastName,
                                            firstName
                                    );
                                    FirebaseUser user = auth.getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                    user.updateProfile(profileUpdates);
                                    Toast.makeText(RegisterActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Register failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private boolean doesNotContainCharacters(String pass) {
        if(!pass.matches(".*[\\d].*") && !pass.matches(".*[a-z].*"))
            return true;
        return false;
    }
}