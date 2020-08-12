package com.example.moovy.repositories;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.moovy.models.Movie;
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
    private List<Movie> movies = new ArrayList<>();

    public static MoviesRepository getInstance() {
        if (instance == null) {
            instance = new MoviesRepository();
        }

        return instance;
    }

    public MutableLiveData<List<Movie>> getMovies() {
        loadMovies();
        MutableLiveData<List<Movie>> movieData = new MutableLiveData<>();
        movieData.setValue(movies);
        return movieData;
    }

    private void loadMovies() {
        db.collection("movies").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                movies.clear();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Movie movie = document.toObject(Movie.class);
                    movie.setId(document.getId());
                    movies.add(movie);
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
}
