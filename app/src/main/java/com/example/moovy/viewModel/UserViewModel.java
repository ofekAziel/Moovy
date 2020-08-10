package com.example.moovy.viewModel;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moovy.models.User;
import com.example.moovy.repositories.UserRepository;

public class UserViewModel extends ViewModel {

    private UserRepository userRepository;
    private MutableLiveData<User> user;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void init(Context context) {
        userRepository = UserRepository.getInstance(context);

        if (user == null) {
            user = userRepository.getUser();
        }
    }

    public LiveData<User> getUser() {
        return user;
    }
}
