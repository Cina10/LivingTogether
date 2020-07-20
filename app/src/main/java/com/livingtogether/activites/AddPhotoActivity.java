package com.livingtogether.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.livingtogether.livingtogether.R;

public class AddPhotoActivity extends AppCompatActivity {
FloatingActionButton fab;
Button btUpload;
ImageView ivProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        fab = findViewById(R.id.fab);
        btUpload= findViewById(R.id.btUpload);
        btUpload.setOnClickListener();
        ivProfile = findViewById(R.id.ivProfile);


    }
}