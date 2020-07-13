package com.example.moovy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moovy.R;
import com.example.moovy.models.Movie;
import com.google.gson.Gson;

public class DetailsActivity extends AppCompatActivity {
    private ImageButton editButton;
    Movie selectedMovie;
    TextView titleTextView, genreTextView, actorsTextView, directorTextView, summaryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        selectedMovie = new Gson().fromJson(intent.getStringExtra("selectedMovie"), Movie.class);

        editButton = (ImageButton) findViewById(R.id.editButton);
        titleTextView = findViewById(R.id.titleTextView);
        genreTextView = findViewById(R.id.genreTextView);
        actorsTextView = findViewById(R.id.actorsTextView);
        directorTextView = findViewById(R.id.directorTextView);
        summaryTextView = findViewById(R.id.summaryTextView);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open edit activity with certain movie
                Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
                intent.putExtra("selectedMovie", new Gson().toJson(DetailsActivity.this.selectedMovie));
                startActivity(intent);
            }
        });

        titleTextView.setText(selectedMovie.getName());
        genreTextView.setText(selectedMovie.getGenre());
        actorsTextView.setText(selectedMovie.getStarring());
        directorTextView.setText(selectedMovie.getDirector());
        summaryTextView.setText(selectedMovie.getSummary());
    }
}