package com.example.moovy.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moovy.models.Movie;
import com.example.moovy.repositories.MoviesRepository;

import java.util.List;

public class MoviesViewModel extends ViewModel {

    private MoviesRepository moviesRepository;
    private MutableLiveData<List<Movie>> movies;

    public void init(Context context) {
        moviesRepository = MoviesRepository.getInstance(context);

        if (movies == null) {
            movies = moviesRepository.getMovies();
        }
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}
