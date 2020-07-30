package com.example.moovy.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.moovy.R;
import com.example.moovy.activities.DetailsActivity;
import com.example.moovy.activities.GlideApp;
import com.example.moovy.models.Movie;
import com.example.moovy.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MoviesAdapter extends BaseAdapter {

    private List<Movie> movies;
    private User user;

    public MoviesAdapter(List<Movie> movies, User user) {
        this.movies = movies;
        this.user = user;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_layout, parent, false);
        movieCardClickListener(view, position);
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
        movieRating.setText(String.valueOf(movies.get(position).getAverageRating()).substring(0, 3));
    }

    private void downloadMoviePhoto(View view, final int photoHash) {
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("moviePhotos/" + photoHash);
        ImageView moviePhoto = view.findViewById(R.id.moviePhoto);
        GlideApp.with(view.getContext()).load(imageReference).into(moviePhoto);
    }

    private void movieCardClickListener(View view, int position) {
        final Movie selectedMovie = (Movie) this.getItem(position);
        ConstraintLayout movieCard = view.findViewById(R.id.movieCard);
        movieCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent mainIntent = new Intent(context, DetailsActivity.class);
                mainIntent.putExtra("selectedMovie", selectedMovie);
                mainIntent.putExtra("user", user);
                context.startActivity(mainIntent);
            }
        });
    }
}
