package com.example.moovy.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;

import com.example.moovy.R;

public class DetailsActivity extends AppCompatActivity {
    private ImageButton editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        editButton = (ImageButton) findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open edit activity with certain movie
                Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
    }
}
