package com.example.moovy.repositories;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.moovy.models.AppLocalDatabase;
import com.example.moovy.models.Movie;
import com.example.moovy.models.MovieDao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MoviesRepository {

    private static MoviesRepository instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MovieDao movieDao = AppLocalDatabase.getInstance().movieDao();

    public static MoviesRepository getInstance() {
        if (instance == null) {
            instance = new MoviesRepository();
        }

        return instance;
    }

    public LiveData<List<Movie>> getMovies() {
        LiveData<List<Movie>> moviesLiveData = movieDao.getAll();
        loadMoviesFromFirebase();
        return moviesLiveData;
    }

    private void loadMoviesFromFirebase() {
        db.collection("movies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Movie movie = document.toObject(Movie.class);
                    movie.setId(document.getId());
                    new AddMovieAsyncTask(movieDao).execute(movie);
                }
            }
        });
    }

    public void addMovie(Movie movie) {
        new AddMovieAsyncTask(movieDao).execute(movie);
        addMovieToFirebase(movie);
    }

    private void addMovieToFirebase(Movie movie) {
        db.collection("movies").add(movie);
    }

    public void updateMovie(Movie movie) {
        new UpdateMovieAsyncTask(movieDao).execute(movie);
        updateMovieInFirebase(movie);
    }

    private void updateMovieInFirebase(Movie movie) {
        db.collection("movies").document(movie.getId()).update(
                "name", movie.getName(),
                "genre", movie.getGenre(),
                "director", movie.getDirector(),
                "starring", movie.getStarring(),
                "photoHash", movie.getPhotoHash(),
                "summary", movie.getSummary());
    }

    public void deleteMovie(Movie movie) {
        new DeleteMovieAsyncTask(movieDao).execute(movie);
        deleteMovieFromFirebase(movie);
    }

    private void deleteMovieFromFirebase(Movie movie) {
        db.collection("movies").document(movie.getId()).delete();
    }

    private static class UpdateMovieAsyncTask extends AsyncTask<Movie, Void, Void> {
        private MovieDao movieDao;

        private UpdateMovieAsyncTask(MovieDao movieDao) {
            this.movieDao = movieDao;
        }
        @Override
        protected Void doInBackground(Movie... movies) {
            movieDao.update(movies[0]);
            return null;
        }
    }

    private static class AddMovieAsyncTask extends AsyncTask<Movie, Void, Void> {
        private MovieDao movieDao;

        private AddMovieAsyncTask(MovieDao movieDao) {
            this.movieDao = movieDao;
        }
        @Override
        protected Void doInBackground(Movie... movies) {
            movieDao.add(movies[0]);
            return null;
        }
    }

    private static class DeleteMovieAsyncTask extends AsyncTask<Movie, Void, Void> {
        private MovieDao movieDao;

        private DeleteMovieAsyncTask(MovieDao movieDao) {
            this.movieDao = movieDao;
        }
        @Override
        protected Void doInBackground(Movie... movies) {
            movieDao.delete(movies[0]);
            return null;
        }
    }
}
