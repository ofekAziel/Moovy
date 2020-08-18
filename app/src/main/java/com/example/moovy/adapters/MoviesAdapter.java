package com.example.moovy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.moovy.R;
import com.example.moovy.activities.FeedFragmentDirections;
import com.example.moovy.activities.GlideApp;
import com.example.moovy.models.Movie;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MoviesAdapter extends BaseAdapter {

    private List<Movie> movies;

    public MoviesAdapter(List<Movie> movies) {
        this.movies = movies;
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

        movieName.setText(movies.get(position).getName());
        movieGenre.setText(movies.get(position).getGenre());
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
                NavController navCtrl = Navigation.findNavController(v);
                FeedFragmentDirections.ActionFeedFragmentToDetailsFragment directions
                        = FeedFragmentDirections.actionFeedFragmentToDetailsFragment (selectedMovie);
                navCtrl.navigate(directions);
            }
        });
    }
}
