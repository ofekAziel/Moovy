package com.example.moovy.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.moovy.models.Movie;
import com.example.moovy.repositories.MoviesRepository;

import java.util.List;

public class MoviesViewModel extends ViewModel {

    private MoviesRepository moviesRepository;
    private LiveData<List<Movie>> movies;

    public void init() {
        moviesRepository = MoviesRepository.getInstance();

        if (movies == null) {
            movies = moviesRepository.getMovies();
        }
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public void addMovie(Movie movie) {
        if (movies != null) {
            moviesRepository.addMovie(movie);
        }
    }

    public void updateMovie(Movie movie) {
        if (movies != null) {
            moviesRepository.updateMovie(movie);
        }
    }

    public void deleteMovie(Movie movie) {
        if (movies != null) {
            moviesRepository.deleteMovie(movie.getId());
        }
    }
}
