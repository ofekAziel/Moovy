package com.example.moovy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moovy.R;

public class DetailsActivity extends AppCompatActivity {
    private ImageButton editButton, returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        returnButton = findViewById(R.id.returnButton);
        editButton = findViewById(R.id.editButton);
        returnButtonClickListener();
        editButtonClickListener();
    }

    private void returnButtonClickListener() {
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void editButtonClickListener() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
    }
}
