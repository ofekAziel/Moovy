package com.example.moovy.viewModel;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moovy.models.Movie;
import com.example.moovy.models.User;
import com.example.moovy.repositories.MainActivityRepository;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private MainActivityRepository mainActivityRepository;
    private MutableLiveData<List<Movie>> movies;
    private MutableLiveData<User> user;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void init(Context context) {
        mainActivityRepository = MainActivityRepository.getInstance(context);

        if (movies == null) {
            movies = mainActivityRepository.getMovies();
        }
        if (user == null) {
            user = mainActivityRepository.getUser();
        }
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public LiveData<User> getUser() {
        return user;
    }
}
