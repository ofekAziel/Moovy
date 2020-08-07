package com.example.moovy.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moovy.R;
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

public class FeedFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    GridView movieGrid;
    TextView currentUser;
    User user;
    Button addMovieButton;
    List<Movie> movies = new ArrayList<>();

    public FeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        movieGrid = getView().findViewById(R.id.movieGrid);
        addMovieButton = getView().findViewById(R.id.addMovieButton);
        currentUser = getView().findViewById(R.id.currentUser);
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
        String userDisplayName = "Hello " + user.getFirstName() + " " + user.getLastName();
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
            public void onClick(View view) {
                NavController navCtrl = Navigation.findNavController(view);

                FeedFragmentDirections.ActionFeedFragmentToUpdateFragment directions
                        = FeedFragmentDirections.actionFeedFragmentToUpdateFragment(new Movie());
                navCtrl.navigate(directions);
            }
        });
                //Navigation.createNavigateOnClickListener(R.id.action_feedFragment_to_updateFragment));
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

                    FeedFragment.CustomAdapter customAdapter = new FeedFragment.CustomAdapter();
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
            return movies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.movie_layout, null);
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
                    NavController navCtrl = Navigation.findNavController(v);
                    FeedFragmentDirections.ActionFeedFragmentToDetailsFragment directions
                            = FeedFragmentDirections.actionFeedFragmentToDetailsFragment (selectedMovie);
                    navCtrl.navigate(directions);
                }
            });
        }
    }
}