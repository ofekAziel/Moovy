package com.example.moovy.repositories;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.moovy.MovieDataLoadListener;
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
    private static Context mContext;
    private static MovieDataLoadListener movieDataLoadListener;

    public static MoviesRepository getInstance(Context context) {
        mContext = context;

        if (instance == null) {
            instance = new MoviesRepository();
        }

        movieDataLoadListener = (MovieDataLoadListener) mContext;
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

                movieDataLoadListener.onMoviesLoad();
            }
        });
    }
}
