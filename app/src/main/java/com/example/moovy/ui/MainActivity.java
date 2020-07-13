package com.example.moovy.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.Serializable;
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

        Movie movie;

        public Movie getMovie() {
            return this.movie;
        }

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
            movieCardClickListener(view);
            downloadMoviePhoto(view, movies.get(position).getPhotoHash());
            setMovieCardFields(position, view);
            return view;
        }

        private void setMovieCardFields(int position, View view) {
            TextView movieName = view.findViewById(R.id.movieName);
            TextView movieGenre = view.findViewById(R.id.movieGenre);
            TextView movieRating = view.findViewById(R.id.movieRating);

            movieName.setText(movies.get(position).getName());
            movieGenre.setText(movies.get(position).getGenre());
            movieRating.setText("4/5");
        }

        private void downloadMoviePhoto(View view, int photoHash) {
            StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("moviePhotos/" + photoHash);
            ImageView moviePhoto = view.findViewById(R.id.moviePhoto);
            GlideApp.with(view.getContext()).load(imageReference).into(moviePhoto);
        }

        private void movieCardClickListener(View view) {
            ConstraintLayout movieCard = view.findViewById(R.id.movieCard);
            movieCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent mainIntent = new Intent(context, DetailsActivity.class);
                    mainIntent.putExtra("selectedMovie", new Gson().toJson(CustomAdapter.this.getMovie()));
                    context.startActivity(mainIntent);
                }
            });
        }
    }
}
