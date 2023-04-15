package com.example.myth;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myth.databinding.ActivityMainBinding;
import com.example.myth.databinding.ActivityRegisterBinding;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.PhantomReference;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ActivityRegisterBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private EditText signupEmail, signupPassword, signupPasswordTwo, signupLastName, signupFirstName;
    private Button signupButton;
    private TextView loginRedirectText;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(R.layout.activity_register);

        initWidgets();
//        auth = FirebaseAuth.getInstance();
//        signupLastName = findViewById(R.id.lastnameEditText);
//        signupFirstName = findViewById(R.id.firstnameEditText);
//        signupEmail = findViewById(R.id.emailRegisterEditText);
//        signupPassword = findViewById(R.id.passwordEditText);
//        signupPasswordTwo = findViewById(R.id.rePasswordEditText);
//        signupButton = findViewById(R.id.registerBtn);
//        loginRedirectText = findViewById(R.id.loginPageBtn);
//        firebaseFirestore = FirebaseFirestore.getInstance();
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
                                            firstName,
                                            null
                                    );
                                    String userId = FirebaseAuth.getInstance().getUid();
                                    firebaseFirestore.collection("User")
                                            .document(userId).set(registeredUser);
                                    FirebaseUser user = auth.getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                    user.updateProfile(profileUpdates);
                                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                    preferenceManager.putString(Constants.KEY_USER_ID, userId);
                                    preferenceManager.putString(Constants.KEY_NAME, firstName);
                                    preferenceManager.putString(Constants.KEY_EMAIL, email);
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

    private void initWidgets() {
        auth = FirebaseAuth.getInstance();
        signupLastName = findViewById(R.id.lastnameEditText);
        signupFirstName = findViewById(R.id.firstnameEditText);
        signupEmail = findViewById(R.id.emailRegisterEditText);
        signupPassword = findViewById(R.id.passwordEditText);
        signupPasswordTwo = findViewById(R.id.rePasswordEditText);
        signupButton = findViewById(R.id.registerBtn);
        loginRedirectText = findViewById(R.id.loginPageBtn);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

//    private String encodeImage(Bitmap bitmap){
//        int previewWidth = 150;
//        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
//        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
//        byte[] bytes = byteArrayOutputStream.toByteArray();
//        return Base64.encodeToString(bytes, Base64.DEFAULT);
//    }

//    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if(result.getResultCode() == RESULT_OK){
//                    if(result.getData() != null) {
//                        Uri imageUri = result.getData().getData();
//                        try {
//                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
//                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                            //binding.imageProfile.setImageBitmap(bitmap);
//                            String encodeImage = encodeImage(bitmap);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//    );

    private boolean doesNotContainCharacters(String pass) {
        if(!pass.matches(".*[\\d].*") && !pass.matches(".*[a-z].*"))
            return true;
        return false;
    }
}