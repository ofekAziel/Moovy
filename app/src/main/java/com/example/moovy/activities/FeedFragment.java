package com.example.moovy.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.moovy.R;
import com.example.moovy.adapters.MoviesAdapter;
import com.example.moovy.models.Movie;
import com.example.moovy.models.User;
import com.example.moovy.viewModel.MoviesViewModel;
import com.example.moovy.viewModel.UserViewModel;
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

    private GridView movieGrid;
    private TextView currentUser;
    private Button addMovieButton;
    private MoviesViewModel moviesViewModel;
    private UserViewModel userViewModel;
    private MoviesAdapter moviesAdapter;

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
        addMovieButtonClickListener();
        moviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        moviesViewModel.init();
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.init();
        addMoviesObservable();
        addUserObservable();
        setMovieAdapter();
    }



    private void addMoviesObservable() {
        moviesViewModel.getMovies().observe(getViewLifecycleOwner(), new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                moviesAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addUserObservable() {
        userViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
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
                NavController navCtrl = Navigation.findNavController(v);
                FeedFragmentDirections.ActionFeedFragmentToUpdateFragment directions
                        = FeedFragmentDirections.actionFeedFragmentToUpdateFragment (new Movie());
                navCtrl.navigate(directions);
            }
        });
    }

    private void setMovieAdapter() {
        moviesAdapter = new MoviesAdapter(moviesViewModel.getMovies().getValue());
        movieGrid.setAdapter(moviesAdapter);
    }
}