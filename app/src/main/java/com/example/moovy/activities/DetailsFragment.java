package com.example.moovy.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moovy.R;
import com.example.moovy.adapters.CommentAdapter;
import com.example.moovy.models.Comment;
import com.example.moovy.models.Movie;
import com.example.moovy.models.User;
import com.example.moovy.viewModel.CommentsViewModel;
import com.example.moovy.viewModel.UserViewModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.List;

public class DetailsFragment extends Fragment {

    private Movie movie;
    private User currentUser;
    private ImageButton editButton, returnButton;
    private TextView titleTextView, genreTextView, actorsTextView, directorTextView, summaryTextView;
    private TextInputLayout commentInput;
    private ImageView imageView;
    private Button submitButton;
    private CommentAdapter commentAdapter;
    private UserViewModel userViewModel;
    private CommentsViewModel commentsViewModel;
    private RecyclerView recyclerView;

    public DetailsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        movie = DetailsFragmentArgs.fromBundle(getArguments()).getMovie();
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.init();
        addUserObservable();
        commentsViewModel = ViewModelProviders.of(this).get(CommentsViewModel.class);
        commentsViewModel.init();
        setUpScreen();
        downloadMoviePhoto(getActivity().getApplicationContext(), movie.getPhotoHash());
        editButtonClickListener();
        returnButtonClickListener();
        commentInput.getEditText().addTextChangedListener(commentTextWatcher);
        addCommentsObservable();
    }

    private void setCommentsAdapter() {
        commentAdapter = new CommentAdapter(getContext(), commentsViewModel.getComments(movie.getDocumentId()).getValue());
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadAfterUserLoad() {
        setUpScreenAdmin();
        submitButtonClickListener();
    }

    private void addUserObservable() {
        userViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                currentUser = users.get(0);
                loadAfterUserLoad();
            }
        });
    }

    private void addCommentsObservable() {
        commentsViewModel.getComments(movie.getDocumentId()).observe(getViewLifecycleOwner(), new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments) {
                setCommentsAdapter();
            }
        });
    }

    private TextWatcher commentTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String commentContent = commentInput.getEditText().getText().toString().trim();
            submitButton.setEnabled(!commentContent.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if(view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    private void submitButtonClickListener() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentsViewModel.addComment(createComment(), movie.getDocumentId());
                commentInput.getEditText().setText("");
                closeKeyboard();
                showToast("Comment submitted");
            }
        });
    }

    public String getFullName(User user) {
        String fullName = user.getFirstName() + " " + user.getLastName();
        return fullName.trim();
    }

    private Comment createComment() {
        return new Comment(commentInput.getEditText().getText().toString(), currentUser.getUserUid(),
                getFullName(currentUser), new Date(), movie.getDocumentId());
    }

    private void downloadMoviePhoto(Context context, final int photoHash) {
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("moviePhotos/" + photoHash);
        GlideApp.with(context).load(imageReference).into(imageView);
    }

    private void setUpScreen() {
        editButton = getView().findViewById(R.id.editButton);
        returnButton = getView().findViewById(R.id.returnButton);
        submitButton = getView().findViewById(R.id.submitButton);
        titleTextView = getView().findViewById(R.id.titleTextView);
        genreTextView = getView().findViewById(R.id.genreTextView);
        actorsTextView = getView().findViewById(R.id.actorsTextView);
        directorTextView = getView().findViewById(R.id.directorTextView);
        summaryTextView = getView().findViewById(R.id.summaryTextView);
        imageView = getView().findViewById(R.id.imageView);
        commentInput = getView().findViewById(R.id.commentInput);
        recyclerView = getView().findViewById(R.id.comments);
        initializeFields();
    }

    private void setUpScreenAdmin() {
        if (!currentUser.isAdmin()) {
            editButton.setVisibility(View.GONE);
        }
    }

    private void initializeFields() {
        titleTextView.setText(movie.getName());
        genreTextView.setText(movie.getGenre());
        actorsTextView.setText(movie.getStarring());
        directorTextView.setText(movie.getDirector());
        summaryTextView.setText(movie.getSummary());
    }

    public void returnButtonClickListener() {
        returnButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Navigation.findNavController(view).popBackStack(R.id.feedFragment,false);
                    }
                }
        );
    }

    private void editButtonClickListener() {
        editButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NavController navCtrl = Navigation.findNavController(view);

                        DetailsFragmentDirections.ActionDetailsFragmentToUpdateFragment directions
                                = DetailsFragmentDirections.actionDetailsFragmentToUpdateFragment(movie);
                        navCtrl.navigate(directions);
                    }
                }
        );
    }
}