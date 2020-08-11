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

import com.example.moovy.MoviesDataLoadListener;
import com.example.moovy.R;
import com.example.moovy.UserDataLoadListener;
import com.example.moovy.adapters.MoviesAdapter;
import com.example.moovy.models.Movie;
import com.example.moovy.models.User;
import com.example.moovy.viewModel.MoviesViewModel;
import com.example.moovy.viewModel.UserViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesDataLoadListener, UserDataLoadListener {

    private GridView movieGrid;
    private TextView currentUser;
    private Button addMovieButton;
    private MoviesViewModel moviesViewModel;
    private UserViewModel userViewModel;
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
        moviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        moviesViewModel.init(MainActivity.this);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.init(MainActivity.this);
        setMovieAdapter();
    }

    @Override
    public void onMoviesLoad() {
        moviesViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                moviesAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onUserLoad() {
        userViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                setUpScreenAdmin();
            }
        });
    }

    private void setUpScreenAdmin() {
        String userDisplayName = "Hello " + userViewModel.getUser().getValue().getFullName();
        currentUser.setText(userDisplayName);

        if (!userViewModel.getUser().getValue().isAdmin()) {
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
        moviesAdapter = new MoviesAdapter(moviesViewModel.getMovies().getValue());
        movieGrid.setAdapter(moviesAdapter);
    }
}
