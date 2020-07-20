package com.livingtogether.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.livingtogether.livingtogether.R;

public class CreateProfileActivity extends AppCompatActivity {
FloatingActionButton fab;
EditText etName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        fab = findViewById(R.id.fab);
        etName = findViewById(R.id.etName);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                Intent i = new Intent(CreateProfileActivity.this, AddPhotoActivity.class);
                i.putExtra("name", name);
                startActivity(i);
            }
        });

    }
}