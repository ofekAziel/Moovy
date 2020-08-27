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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.moovy.R;
import com.example.moovy.models.Movie;
import com.example.moovy.services.Utilities;
import com.example.moovy.viewModel.CommentsViewModel;
import com.example.moovy.viewModel.MoviesViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class UpdateFragment extends Fragment {

    private ImageView imageView;
    private EditText nameInput, genreInput, directorInput, starringInput, summaryInput;
    private Button updateButton, deleteButton, cancelButton;
    private Movie movie;
    private MoviesViewModel moviesViewModel;
    private CommentsViewModel commentsViewModel;

    public UpdateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        moviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        moviesViewModel.init();
        commentsViewModel = ViewModelProviders.of(this).get(CommentsViewModel.class);
        commentsViewModel.init();
        Movie selectedMovie = UpdateFragmentArgs.fromBundle(getArguments()).getMovie();
        initMovie(selectedMovie);
        initFields();
        cancelButtonClickListener();
        imageViewClickListener();
        updateButtonClickListener();
        deleteButtonClickListener();
    }

    public boolean isNewMovie(Movie movie) {
        return movie.getDocumentId().equals("");
    }

    private void initMovie(Movie selectedMovie) {
        if(!isNewMovie(selectedMovie)) {
            this.movie = selectedMovie;
        } else {
            this.movie = new Movie();
        }
    }

    public void deletePhoto() {
        // don't delete default photo
        if(this.movie.getPhotoHash() == 0)
            return;
        FirebaseStorage.getInstance().getReference().child("moviePhotos/" + this.movie.getPhotoHash()).delete();
    }

    private void deleteButtonClickListener() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePhoto();
                commentsViewModel.deleteMovieComments(movie.getDocumentId());
                moviesViewModel.deleteMovie(movie);
                Navigation.findNavController(view).popBackStack(R.id.feedFragment,false);
            }
        });
    }

    private void cancelButtonClickListener() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack(R.id.feedFragment,false);
            }
        });
    }

    private void imageViewClickListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(getActivity());
            }
        });
    }

    public boolean isMovieProper(Movie movie) {
        if(movie.getName().compareTo("") == 0)
            return false;
        if(movie.getGenre().compareTo("") == 0)
            return false;
        if(movie.getDirector().compareTo("") == 0)
            return false;
        if(movie.getStarring().compareTo("") == 0)
            return false;
        if(movie.getSummary().compareTo("") == 0)
            return false;
        return true;
    }

    private void updateButtonClickListener() {
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMovie();
                if (isMovieProper(movie)) {
                    if(isNewMovie(movie)) {
                        moviesViewModel.addMovie(movie);
                    } else {
                        moviesViewModel.updateMovie(movie);
                    }
                } else {
                    showToast("All fields must not be empty");
                }

                addImageToDb(view);
            }
        });
    }

    private void addImageToDb(final View view) {
        Utilities.makeSpinner(getActivity());
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("moviePhotos/" + movie.getPhotoHash());

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Utilities.removeSpinner();
                Navigation.findNavController(view).popBackStack(R.id.feedFragment,false);
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

    private void loadImageFromFile(int photoHash)
    {
        /*try {
            // TODO: get photo by safe args
            //Bitmap bitmap = BitmapFactory.decodeStream(getApplicationContext().openFileInput(fileName));
            //imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("moviePhotos/" + photoHash);
        GlideApp.with(this).load(imageReference).into(imageView);
    }

    private void initFields() {
        imageView = getView().findViewById(R.id.imageView2);
        nameInput = getView().findViewById(R.id.nameInput);
        genreInput = getView().findViewById(R.id.genreInput);
        directorInput = getView().findViewById(R.id.directorInput);
        starringInput = getView().findViewById(R.id.starringInput);
        summaryInput = getView().findViewById(R.id.summaryInput);
        updateButton = getView().findViewById(R.id.updateButton);
        deleteButton = getView().findViewById(R.id.deleteButton);
        cancelButton = getView().findViewById(R.id.cancelButton);
        if (!isNewMovie(movie)) {
            nameInput.setText(movie.getName());
            genreInput.setText(movie.getGenre());
            directorInput.setText(movie.getDirector());
            starringInput.setText(movie.getStarring());
            summaryInput.setText(movie.getSummary());
            loadImageFromFile(movie.getPhotoHash());
        }
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
                getActivity(),
                message,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(selectedImage,
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