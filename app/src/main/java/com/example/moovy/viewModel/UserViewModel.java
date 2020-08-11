package com.example.moovy.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moovy.models.User;
import com.example.moovy.repositories.UserRepository;

public class UserViewModel extends ViewModel {

    private UserRepository userRepository;
    private MutableLiveData<User> user;

    public void init(Context context) {
        userRepository = UserRepository.getInstance(context);

        if (user == null) {
            user = userRepository.getUser();
        }
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void addUser(User user, String userUid) {
        if (user != null) {
            userRepository.addUser(user, userUid);
        }
    }
}
