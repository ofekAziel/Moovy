package com.example.moovy;

    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;

    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.database.Cursor;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.Toast;

    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.firestore.DocumentReference;
    import com.google.firebase.firestore.FirebaseFirestore;

    import java.io.Console;
    import java.io.DataInput;
    import java.io.File;
    import java.util.Date;

    import models.Movie;

public class EditActivity extends AppCompatActivity {
    private ImageView imageView;

    private EditText nameInput, genreInput, directorInput, starringInput, summaryInput;
    private Button updateButton, deleteButton;
    private Movie movie;
    //private DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);
        imageView = (ImageView) findViewById(R.id.imageView2);
        nameInput = (EditText) findViewById(R.id.nameInput);
        genreInput = (EditText) findViewById(R.id.genreInput);
        directorInput = (EditText) findViewById(R.id.directorInput);
        starringInput = (EditText) findViewById(R.id.starringInput);
        summaryInput = (EditText) findViewById(R.id.summaryInput);
        updateButton = (Button) findViewById(R.id.updateButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        movie = new Movie(
                nameInput.getText().toString().trim(),
                genreInput.getText().toString().trim(),
                directorInput.getText().toString().trim(),
                starringInput.getText().toString().trim(),
                summaryInput.getText().toString());
        movie = new Movie();
        //reff = FirebaseDatabase.getInstance().getReference();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(EditActivity.this);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movie.setName(nameInput.getText().toString().trim());
                movie.setGenre(genreInput.getText().toString().trim());
                movie.setDirector(directorInput.getText().toString().trim());
                movie.setStarring(starringInput.getText().toString().trim());
                movie.setSummary(summaryInput.getText().toString());

                db.collection("movies").add(movie)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(
                                        EditActivity.this,
                                        "Movie added",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });

            }
        });

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
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
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
                                // TODO: error when picking gallery image in next line, probably permission issues
                                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                /*File imgFile = new File(picturePath);
                                if(imgFile.exists()) {
                                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                    imageView.setImageBitmap(myBitmap);
                                }*/
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }

}
