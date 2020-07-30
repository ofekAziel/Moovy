package com.example.moovy.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.moovy.R;
import com.example.moovy.adapters.MoviesAdapter;
import com.example.moovy.models.Movie;
import com.example.moovy.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    GridView movieGrid;
    TextView currentUser;
    User user;
    Button addMovieButton;
    List<Movie> movies = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieGrid = findViewById(R.id.movieGrid);
        addMovieButton = findViewById(R.id.addMovieButton);
        currentUser = findViewById(R.id.currentUser);
        getCurrentUser();
        addMovieButtonClickListener();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        movies.clear();
        selectAllMovies();
    }

    private void setUpScreenAdmin() {
        String userDisplayName = "Hello " + user.getFullName();
        currentUser.setText(userDisplayName);

        if (!user.isAdmin()) {
            addMovieButton.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getCurrentUser() {
        String currentUserUid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        db.collection("users").whereEqualTo("userUid", currentUserUid).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                setUpScreenAdmin();
            }
        });
    }

    private void addMovieButtonClickListener() {
        addMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, EditActivity.class);
                MainActivity.this.startActivity(mainIntent);
            }
        });
    }

    private void selectAllMovies() {
        db.collection("movies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Movie movie = document.toObject(Movie.class);
                        movie.setId(document.getId());
                        movies.add(movie);
                    }

                    MoviesAdapter moviesAdapter = new MoviesAdapter(movies, user);
                    movieGrid.setAdapter(moviesAdapter);
                }
            }
        });
    }
}
