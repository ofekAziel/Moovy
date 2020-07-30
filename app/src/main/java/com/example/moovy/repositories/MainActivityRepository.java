package com.example.moovy.repositories;

import android.content.Context;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.example.moovy.DataLoadListener;
import com.example.moovy.models.Movie;
import com.example.moovy.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivityRepository {

    private static MainActivityRepository instance;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Movie> movies = new ArrayList<>();
    private User user;
    private static Context mContext;
    private static DataLoadListener dataLoadListener;

    public static MainActivityRepository getInstance(Context context) {
        mContext = context;

        if (instance == null) {
            instance = new MainActivityRepository();
        }

        dataLoadListener = (DataLoadListener) mContext;
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
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Movie movie = document.toObject(Movie.class);
                    movie.setId(document.getId());
                    movies.add(movie);
                }

                dataLoadListener.onMoviesLoad();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public MutableLiveData<User> getUser() {
        loadUser();
        MutableLiveData<User> userData = new MutableLiveData<>();
        userData.setValue(user);
        return userData;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadUser() {
        String currentUserUid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        db.collection("users").whereEqualTo("userUid", currentUserUid).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                dataLoadListener.onUserLoad();
            }
        });
    }
}
