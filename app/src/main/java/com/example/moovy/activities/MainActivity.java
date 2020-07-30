package com.example.moovy.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.moovy.DataLoadListener;
import com.example.moovy.R;
import com.example.moovy.adapters.MoviesAdapter;
import com.example.moovy.models.Movie;
import com.example.moovy.models.User;
import com.example.moovy.viewModel.MainActivityViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DataLoadListener {

    private GridView movieGrid;
    private TextView currentUser;
    private User user;
    private Button addMovieButton;
    private MainActivityViewModel mainActivityViewModel;
    private MoviesAdapter moviesAdapter;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieGrid = findViewById(R.id.movieGrid);
        addMovieButton = findViewById(R.id.addMovieButton);
        currentUser = findViewById(R.id.currentUser);
        addMovieButtonClickListener();
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mainActivityViewModel.init(MainActivity.this);
        setMovieAdapter();
    }

    @Override
    public void onMoviesLoad() {
        mainActivityViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                moviesAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onUserLoad() {
        mainActivityViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                setUpScreenAdmin();
            }
        });
    }

    private void setUpScreenAdmin() {
        String userDisplayName = "Hello " + mainActivityViewModel.getUser().getValue().getFullName();
        currentUser.setText(userDisplayName);

        if (!mainActivityViewModel.getUser().getValue().isAdmin()) {
            addMovieButton.setVisibility(View.GONE);
        }
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

    private void setMovieAdapter() {
        moviesAdapter = new MoviesAdapter(mainActivityViewModel.getMovies().getValue(), mainActivityViewModel.getUser().getValue());
        movieGrid.setAdapter(moviesAdapter);
    }
}
