package com.example.moovy.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.moovy.R;
import com.example.moovy.models.Movie;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class EditActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText nameInput, genreInput, directorInput, starringInput, summaryInput;
    private Button updateButton, deleteButton, cancelButton;
    private Movie movie;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Movie selectedMovie = (Movie) getIntent().getSerializableExtra("selectedMovie");
        if(selectedMovie != null)
            this.movie = selectedMovie;
        else
            this.movie = new Movie();
        initFields();
        cancelButtonClickListener();
        imageViewClickListener();
        updateButtonClickListener();
        deleteButtonClickListener();
    }

    public void deletePhoto() {
        // don't delete default photo
        if(this.movie.getPhotoHash() == 0)
            return;
        FirebaseStorage.getInstance().getReference().child("moviePhotos/" + this.movie.getPhotoHash()).delete();
    }

    public void deleteMovie() {
        deletePhoto();
        db.collection("movies").document(this.movie.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("Movie deleted");
            }
        });
    }

    private void deleteButtonClickListener() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMovie();
                Intent mainIntent = new Intent(EditActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                EditActivity.this.startActivity(mainIntent);
            }
        });
    }

    private void cancelButtonClickListener() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void imageViewClickListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(EditActivity.this);
            }
        });
    }

    private void updateButtonClickListener() {
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(movie.isNewMovie()) {
                    setMovie();
                    addMovieToDb();
                }
                else {
                    setMovie();
                    updateMovieInDb();
                }
                addImageToDb();
                Intent mainIntent = new Intent(EditActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                EditActivity.this.startActivity(mainIntent);
            }
        });
    }

    private void addImageToDb() {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("moviePhotos/" + movie.getPhotoHash());

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                showToast("Failed to upload image");
            }
        });
    }

    private void updateMovieInDb() {
        db.collection("movies").document(this.movie.getId()).update(
                "name", movie.getName(),
                "genre", movie.getGenre(),
                "director", movie.getDirector(),
                "starring", movie.getStarring(),
                "photoHash", movie.getPhotoHash(),
                "summary", movie.getSummary()).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("Movie updated");
                    }
                });
    }

    private void addMovieToDb() {
        db.collection("movies").add(movie)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    showToast("Movie added");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast("Failed to add movie");
                }
            });
    }

    private void setMovie() {
        movie.setName(nameInput.getText().toString().trim());
        movie.setGenre(genreInput.getText().toString().trim());
        movie.setDirector(directorInput.getText().toString().trim());
        movie.setStarring(starringInput.getText().toString().trim());
        movie.setSummary(summaryInput.getText().toString());

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        movie.setPhotoHash(Arrays.hashCode(data) + movie.hashCode());
    }

    private void loadImageFromFile(String fileName)
    {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getApplicationContext().openFileInput(fileName));
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initFields() {
        setContentView(R.layout.activity_edit);
        imageView = findViewById(R.id.imageView2);
        nameInput = findViewById(R.id.nameInput);
        genreInput = findViewById(R.id.genreInput);
        directorInput = findViewById(R.id.directorInput);
        starringInput = findViewById(R.id.starringInput);
        summaryInput = findViewById(R.id.summaryInput);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        cancelButton = findViewById(R.id.cancelButton);
        if (movie != null) {
            nameInput.setText(movie.getName());
            genreInput.setText(movie.getGenre());
            directorInput.setText(movie.getDirector());
            starringInput.setText(movie.getStarring());
            summaryInput.setText(movie.getSummary());
            loadImageFromFile(String.valueOf(movie.getPhotoHash()));
        }
    }

    private Context getContext() {
        return this;
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose the movie picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                        } else {
                            // No explanation needed; request the permission
                            ActivityCompat.requestPermissions((Activity) getContext(),
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    1660); // I hope this is the right code
                        }
                    }

                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(
                EditActivity.this,
                message,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        imageView.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                // error when picking gallery image in next line, probably permission issues
                                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                File imgFile = new File(picturePath);
                                if(imgFile.exists()) {
                                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                    imageView.setImageBitmap(myBitmap);
                                }
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }
}
