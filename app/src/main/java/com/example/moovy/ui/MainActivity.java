package com.example.moovy.ui;

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
import com.example.moovy.models.Movie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    GridView movieGrid;
    Button addMovieButton;
    ConstraintLayout movieCard;
    List<Movie> movies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        movieGrid = findViewById(R.id.movieGrid);
        addMovieButton = findViewById(R.id.addMovieButton);
        selectAllMovies();
        addMovieButtonClickListener();
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
                        movies.add(document.toObject(Movie.class));
                    }

                    CustomAdapter customAdapter = new CustomAdapter();
                    movieGrid.setAdapter(customAdapter);
                }
            }
        });
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return movies.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.movie_layout, null);
            ConstraintLayout movieCard = view.findViewById(R.id.movieCard);
            movieCardClickListener(movieCard);
            ImageView moviePhoto = view.findViewById(R.id.moviePhoto);
            TextView movieName = view.findViewById(R.id.movieName);
            TextView movieGenre = view.findViewById(R.id.movieGenre);
            TextView movieRating = view.findViewById(R.id.movieRating);

//            moviePhoto.setImageResource("");
            movieName.setText(movies.get(position).getName());
            movieGenre.setText(movies.get(position).getGenre());
//            movieRating.setText();
            return view;
        }

        private void movieCardClickListener(ConstraintLayout movieCard) {
            movieCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
