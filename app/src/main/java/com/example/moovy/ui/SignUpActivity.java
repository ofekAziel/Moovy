package com.example.moovy.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moovy.R;
import com.example.moovy.models.User;
import com.example.moovy.services.ValidationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private EditText firstName, lastName, username, password;
    private Button signUpButton;
    private FirebaseAuth firebaseAuth;
    private ValidationService validationService = new ValidationService();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signUpButton = findViewById(R.id.signUp);
        signUpButtonClickListener();
    }

    private void signUpButtonClickListener() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(firstName.getText().toString(), lastName.getText().toString());
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();

                if(user.getFirstName().isEmpty()) {
                    firstName.setError("Please enter a first name");
                    firstName.requestFocus();
                }
                else if(user.getLastName().isEmpty()) {
                    lastName.setError("Please enter a last name");
                    lastName.requestFocus();
                }
                else if (!validationService.isUserNameValid(usernameText)) {
                    username.setError("Please enter a valid Email address");
                    username.requestFocus();
                } else if (!validationService.isPasswordValid(passwordText)) {
                    password.setError("Password must contain at least 5 characters");
                    password.requestFocus();
                } else {
                    signUpFirebase(usernameText, passwordText, user);
                }
            }
        });
    }

    private void signUpFirebase(String usernameText, String passwordText, final User user) {
        firebaseAuth.createUserWithEmailAndPassword(usernameText, passwordText).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Cannot signUp", Toast.LENGTH_SHORT).show();
                } else {
                    String userUid = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                    saveUserToDatabase(user, userUid);
                    Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                    SignUpActivity.this.startActivity(mainIntent);
                }
            }
        });
    }

    private void saveUserToDatabase(User user, String userUid) {
        user.setUserUid(userUid);
        user.setAdmin(false);
        db.collection("users").add(user).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Cannot save user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
