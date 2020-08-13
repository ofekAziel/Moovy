package com.example.moovy.repositories;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moovy.models.AppLocalDatabase;
import com.example.moovy.models.Movie;
import com.example.moovy.models.MovieDao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MoviesRepository {

    private static MoviesRepository instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MovieDao movieDao = AppLocalDatabase.getInstance().movieDao();
    private List<Movie> movies = new ArrayList<>();

    public static MoviesRepository getInstance() {
        if (instance == null) {
            instance = new MoviesRepository();
        }

        return instance;
    }

    public LiveData<List<Movie>> getMovies() {
        LiveData<List<Movie>> moviesLiveData = movieDao.getAll();
        loadMovies();
        return moviesLiveData;
    }

    private void loadMovies() {
        db.collection("movies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Movie movie = document.toObject(Movie.class);
                    movie.setId(document.getId());
                    new InsertMovieAsyncTask(movieDao).execute(movie);
                }
            }
        });
    }

    public void addMovie(Movie movie) {
        db.collection("movies").add(movie);
    }

    public void updateMovie(Movie movie) {
        db.collection("movies").document(movie.getId()).update(
                "name", movie.getName(),
                "genre", movie.getGenre(),
                "director", movie.getDirector(),
                "starring", movie.getStarring(),
                "photoHash", movie.getPhotoHash(),
                "summary", movie.getSummary());
    }

    public void deleteMovie(String movieId) {
        db.collection("movies").document(movieId).delete();
    }

    private static class InsertMovieAsyncTask extends AsyncTask<Movie, Void, Void> {
        private MovieDao movieDao;

        private InsertMovieAsyncTask(MovieDao movieDao) {
            this.movieDao = movieDao;
        }
        @Override
        protected Void doInBackground(Movie... movies) {
            movieDao.add(movies[0]);
            return null;
        }
    }
}
