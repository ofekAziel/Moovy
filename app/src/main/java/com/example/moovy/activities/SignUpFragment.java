package com.example.moovy.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.moovy.R;
import com.example.moovy.models.User;
import com.example.moovy.services.Utilities;
import com.example.moovy.services.ValidationService;
import com.example.moovy.viewModel.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignUpFragment extends Fragment {

    private EditText firstName, lastName, username, password;
    private Button signUpButton;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ValidationService validationService = new ValidationService();
    private UserViewModel userViewModel;

    public SignUpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firstName = getView().findViewById(R.id.firstName);
        lastName = getView().findViewById(R.id.lastName);
        username = getView().findViewById(R.id.username);
        password = getView().findViewById(R.id.password);
        signUpButton = getView().findViewById(R.id.signUp);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.init();
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
        Utilities.makeSpinner(getActivity());
        firebaseAuth.createUserWithEmailAndPassword(usernameText, passwordText).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Utilities.removeSpinner();
                    Toast.makeText(getContext(), "Cannot sign up", Toast.LENGTH_SHORT).show();
                } else {
                    String userUid = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                    userViewModel.addUser(user, userUid);
                    Utilities.removeSpinner();
                    Navigation.findNavController(getView()).navigate(R.id.action_signUpFragment_to_feedFragment);
                }
            }
        });
    }
}