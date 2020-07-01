package com.example.moovy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moovy.R;
import com.example.moovy.services.ValidationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText username, password;
    private Button signUpButton;
    private FirebaseAuth firebaseAuth;
    private ValidationService validationService = new ValidationService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signUpButton = findViewById(R.id.signUp);
        signUpButtonClickListener();
    }

    private void signUpButtonClickListener() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();

                if (!validationService.isUserNameValid(usernameText)) {
                    username.setError("Not valid username");
                    username.requestFocus();
                } else if (!validationService.isPasswordValid(passwordText)) {
                    password.setError("Not valid password");
                    password.requestFocus();
                } else {
                    signUpFirebase(usernameText, passwordText);
                }
            }
        });
    }

    private void signUpFirebase(String usernameText, String passwordText) {
        firebaseAuth.createUserWithEmailAndPassword(usernameText, passwordText).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Cannot signUp", Toast.LENGTH_SHORT).show();
                } else {
                    Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                    SignUpActivity.this.startActivity(mainIntent);
                }
            }
        });
    }
}
