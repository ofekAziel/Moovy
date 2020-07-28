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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moovy.R;
import com.example.moovy.models.Movie;
import com.example.moovy.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DetailsActivity extends AppCompatActivity {
    private ImageButton editButton;
    Movie movie;
    User user;
    Bitmap bitmap;
    TextView titleTextView, genreTextView, actorsTextView, directorTextView, summaryTextView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        movie = (Movie) getIntent().getSerializableExtra("selectedMovie");
        user = (User) getIntent().getSerializableExtra("user");

        setUpScreen();
        editButtonClickListener();
        downloadMoviePhoto(getApplicationContext(), movie.getPhotoHash());
    }

    private void downloadMoviePhoto(Context context, final int photoHash) {
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("moviePhotos/" + photoHash);
        GlideApp.with(context).load(imageReference).into(imageView);
    }

    private void setUpScreen() {
        editButton = findViewById(R.id.editButton);
        titleTextView = findViewById(R.id.titleTextView);
        genreTextView = findViewById(R.id.genreTextView);
        actorsTextView = findViewById(R.id.actorsTextView);
        directorTextView = findViewById(R.id.directorTextView);
        summaryTextView = findViewById(R.id.summaryTextView);
        imageView = findViewById(R.id.imageView);
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