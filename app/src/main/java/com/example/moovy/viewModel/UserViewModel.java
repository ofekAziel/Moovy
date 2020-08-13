package com.example.moovy.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moovy.models.User;
import com.example.moovy.repositories.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel {

    private UserRepository userRepository;
    private LiveData<List<User>> user;

    public void init() {
        userRepository = UserRepository.getInstance();
    }

    public LiveData<List<User>> getUser() {
        if (user == null) {
            user = userRepository.getUser();
        }

        return user;
    }

    public void addUser(User user, String userUid) {
        if (user != null) {
            userRepository.addUser(user, userUid);
        }
    }
}
