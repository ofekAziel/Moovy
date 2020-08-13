package com.example.moovy.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moovy.R;
import com.example.moovy.adapters.CommentAdapter;
import com.example.moovy.models.Comment;
import com.example.moovy.models.Movie;
import com.example.moovy.models.User;
import com.example.moovy.models.UserRating;
import com.example.moovy.viewModel.CommentsViewModel;
import com.example.moovy.viewModel.UserViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Movie movie;
    private User currentUser;
    private UserRating userRating;
    private RatingBar ratingBar;
    private ImageButton editButton, returnButton;
    private TextView titleTextView, genreTextView, actorsTextView, directorTextView, summaryTextView, averageRating;
    private TextInputLayout commentInput;
    private ImageView imageView;
    private Button submitButton;
    private CommentAdapter commentAdapter;
    private UserViewModel userViewModel;
    private CommentsViewModel commentsViewModel;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        movie = (Movie) getIntent().getSerializableExtra("selectedMovie");
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.init();
        commentsViewModel = ViewModelProviders.of(this).get(CommentsViewModel.class);
        commentsViewModel.init();
        setUpScreen();
        editButtonClickListener();
        returnButtonClickListener();
        downloadMoviePhoto(getApplicationContext(), movie.getPhotoHash());
        commentInput.getEditText().addTextChangedListener(commentTextWatcher);
        addCommentsObservable();
//        addUserObservable();
        initRecyclerView();
    }

    private void loadAfterUserLoad() {
        setUpScreenAdmin();
        getRating();
        ratingBarChangeListener();
        submitButtonClickListener();
    }

//    private void addUserObservable() {
//        userViewModel.getUser().observe(this, new Observer<User>() {
//            @Override
//            public void onChanged(User user) {
//                currentUser = user;
//                loadAfterUserLoad();
//            }
//        });
//    }

    private void addCommentsObservable() {
        commentsViewModel.getComments(movie.getId()).observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments) {
                commentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.comments);;
        commentAdapter = new CommentAdapter(this, commentsViewModel.getComments(movie.getId()).getValue());
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    private void showToast(String message) {
        Toast toast = Toast.makeText(DetailsActivity.this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void submitButtonClickListener() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentsViewModel.addComment(createComment(), movie.getId());
                commentInput.getEditText().setText("");
                closeKeyboard();
                showToast("Comment submitted");
            }
        });
    }

    private Comment createComment() {
        return new Comment(commentInput.getEditText().getText().toString(), currentUser, new Date());
    }

    private void ratingBarChangeListener() {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                updateAverageRating(rating);
                addRating(rating);
            }
        });
    }

    private void updateAverageRating(final float rating) {
        final float prevUserRating = userRating == null ? (float) 0.0 : userRating.getRating();
        db.collection("movies").document(movie.getId())
                .collection("userRatings").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numOfRatings = queryDocumentSnapshots.size();
                float numerator = movie.getAverageRating() * numOfRatings + rating - prevUserRating;
                int divider = userRating == null ? numOfRatings + 1 : numOfRatings;
                movie.setAverageRating(numerator / divider);
                averageRating.setText(String.valueOf(movie.getAverageRating()).substring(0, 3));
                db.collection("movies").document(movie.getId()).update("averageRating", movie.getAverageRating());
            }
        });
    }

    private void addRating(float rating) {
        if (userRating == null) {
            userRating = new UserRating(currentUser.getUserUid(), currentUser, rating);
        } else {
            userRating.setRating(rating);
        }

        db.collection("movies").document(movie.getId()).collection("userRatings")
                .document(userRating.getId()).set(userRating);
    }

    private void getRating() {
        db.collection("movies").document(movie.getId()).collection("userRatings")
                .document(currentUser.getUserUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userRating = documentSnapshot.toObject(UserRating.class);
                ratingBar.setRating(userRating == null ? (float) 0.0 : userRating.getRating());
            }
        });
    }

    private void downloadMoviePhoto(Context context, final int photoHash) {
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("moviePhotos/" + photoHash);
        GlideApp.with(context).load(imageReference).into(imageView);
    }

    private void setUpScreen() {
        editButton = findViewById(R.id.editButton);
        returnButton = findViewById(R.id.returnButton);
        submitButton = findViewById(R.id.submitButton);
        titleTextView = findViewById(R.id.titleTextView);
        genreTextView = findViewById(R.id.genreTextView);
        actorsTextView = findViewById(R.id.actorsTextView);
        directorTextView = findViewById(R.id.directorTextView);
        summaryTextView = findViewById(R.id.summaryTextView);
        ratingBar = findViewById(R.id.ratingBar);
        imageView = findViewById(R.id.imageView);
        averageRating = findViewById(R.id.averageRating);
        commentInput = findViewById(R.id.commentInput);
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
        averageRating.setText(String.valueOf(movie.getAverageRating()).substring(0, 3));
    }

    public void saveImageToFile(String fileName) {
        try {
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
    }

    private void editButtonClickListener() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
                intent.putExtra("selectedMovie", movie);
                saveImageToFile(String.valueOf(movie.getPhotoHash()));
                startActivity(intent);
            }
        });
    }

    private void returnButtonClickListener() {
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}