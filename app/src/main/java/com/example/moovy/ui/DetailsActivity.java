package com.example.moovy.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moovy.R;
import com.example.moovy.models.Movie;
import com.example.moovy.models.User;
import com.example.moovy.models.UserRating;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class DetailsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageButton editButton;
    Movie movie;
    User user;
    UserRating userRating;
    Bitmap bitmap;
    TextView titleTextView, genreTextView, actorsTextView, directorTextView, summaryTextView, averageRating;
    ImageView imageView;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        movie = (Movie) getIntent().getSerializableExtra("selectedMovie");
        user = (User) getIntent().getSerializableExtra("user");
        getRating();
        setUpScreen();
        editButtonClickListener();
        ratingBarChangeListener();
        downloadMoviePhoto(getApplicationContext(), movie.getPhotoHash());
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
                db.collection("movies").document(movie.getId()).update("averageRating", movie.getAverageRating());
            }
        });
    }

    private void addRating(float rating) {
        if (userRating == null) {
            userRating = new UserRating(user.getUserUid(), user, rating);
        } else {
            userRating.setRating(rating);
        }

        db.collection("movies").document(movie.getId()).collection("userRatings")
                .document(userRating.getId()).set(userRating);
    }

    private void getRating() {
        db.collection("movies").document(movie.getId()).collection("userRatings")
                .document(user.getUserUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
        titleTextView = findViewById(R.id.titleTextView);
        genreTextView = findViewById(R.id.genreTextView);
        actorsTextView = findViewById(R.id.actorsTextView);
        directorTextView = findViewById(R.id.directorTextView);
        summaryTextView = findViewById(R.id.summaryTextView);
        ratingBar = findViewById(R.id.ratingBar);
        imageView = findViewById(R.id.imageView);
        averageRating = findViewById(R.id.averageRating);
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
        averageRating.setText(String.valueOf(movie.getAverageRating()).substring(0, 3));
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