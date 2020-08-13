package com.example.moovy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.moovy.R;
import com.example.moovy.services.ValidationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInFragment extends Fragment {
    private EditText username, password;
    private Button loginButton, signUpButton;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ValidationService validationService = new ValidationService();

    public LogInFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        username = getView().findViewById(R.id.username);
        password = getView().findViewById(R.id.password);
        loginButton = getView().findViewById(R.id.login);
        signUpButton = getView().findViewById(R.id.register);
        loginButtonClickListener();
        signUpButtonClickListener();
    }

    private void loginButtonClickListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
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
                    loginFirebase(usernameText, passwordText);
                }
            }
        });
    }

    private void loginFirebase(String usernameText, String passwordText) {
        firebaseAuth.signInWithEmailAndPassword(usernameText, passwordText).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Cannot login", Toast.LENGTH_SHORT).show();
                } else {
                    Navigation.findNavController(getView()).navigate(R.id.action_logInFragment_to_feedFragment);
                }
            }
        });
    }

    private void signUpButtonClickListener() {
        signUpButton.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_logInFragment_to_signUpFragment));
    }
}