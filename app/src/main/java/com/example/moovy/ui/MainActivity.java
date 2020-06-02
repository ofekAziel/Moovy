package com.example.moovy.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moovy.R;

public class MainActivity extends AppCompatActivity {

    ImageView moviePhoto;
    TextView movieName, movieGenre, movieRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moviePhoto = findViewById(R.id.moviePhoto);
        movieName = findViewById(R.id.movieName);
        movieGenre = findViewById(R.id.movieGenre);
        movieRating = findViewById(R.id.movieRating);
    }
}
