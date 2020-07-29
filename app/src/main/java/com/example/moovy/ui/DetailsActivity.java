package com.example.moovy.ui;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moovy.R;
import com.example.moovy.models.Comment;
import com.example.moovy.models.Movie;
import com.example.moovy.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageButton editButton;
    Movie movie;
    User user;
    Bitmap bitmap;
    TextView titleTextView, genreTextView, actorsTextView, directorTextView, summaryTextView;
    TextInputLayout commentInput;
    ImageView imageView;
    Button submitButton;

    private ArrayList<Comment> comments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        movie = (Movie) getIntent().getSerializableExtra("selectedMovie");
        user = (User) getIntent().getSerializableExtra("user");
        initRecyclerView();
        setUpScreen();
        editButtonClickListener();
        submitButtonClickListener();
        downloadMoviePhoto(getApplicationContext(), movie.getPhotoHash());
        commentInput.getEditText().addTextChangedListener(commentTextWatcher);

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

    private Comment createComment() {
        return new Comment(commentInput.getEditText().getText().toString(), user, new Date());
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(
                DetailsActivity.this,
                message,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    private void addComment() {
        Comment comment = createComment();
        db
                .collection("movies").document(movie.getId())
                .collection("comments").add(comment);

        comments.add(comment);
        commentInput.getEditText().setText("");
        closeKeyboard();
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
                addComment();
                showToast("Comment submitted");
            }
        });
    }

    private void getComments() {
        db
                .collection("movies").document(movie.getId())
                .collection("comments").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Comment comment = document.toObject(Comment.class);
                                comment.setId(document.getId());
                                comments.add(comment);
                            }
                        }
                    }
                });
    }

    private void initRecyclerView() {
        getComments();
        RecyclerView recyclerView = findViewById(R.id.comments);
        CommentAdapter commentAdapter = new CommentAdapter(this, comments);
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void downloadMoviePhoto(Context context, final int photoHash) {
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("moviePhotos/" + photoHash);
        GlideApp.with(context).load(imageReference).into(imageView);
    }

    private void setUpScreen() {
        editButton = findViewById(R.id.editButton);
        submitButton = findViewById(R.id.submitButton);
        titleTextView = findViewById(R.id.titleTextView);
        genreTextView = findViewById(R.id.genreTextView);
        actorsTextView = findViewById(R.id.actorsTextView);
        directorTextView = findViewById(R.id.directorTextView);
        summaryTextView = findViewById(R.id.summaryTextView);
        imageView = findViewById(R.id.imageView);
        commentInput = findViewById(R.id.commentInput);
        setUpScreenAdmin();
        initializeFields();
    }

    private void setUpScreenAdmin() {
        if (!user.isAdmin()) {
            editButton.setVisibility(View.GONE);
        }
    }

    private void initializeFields() {
        titleTextView.setText(movie.getName());
        genreTextView.setText(movie.getGenre());
        actorsTextView.setText(movie.getStarring());
        directorTextView.setText(movie.getDirector());
        summaryTextView.setText(movie.getSummary());
        imageView.setImageBitmap(bitmap);
    }

    public String saveImageToFile(String fileName) {
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
        return fileName;
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
}