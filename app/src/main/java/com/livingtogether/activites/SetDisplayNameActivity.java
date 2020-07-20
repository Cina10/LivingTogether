package com.livingtogether.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;

public class SetDisplayNameActivity extends AppCompatActivity {
    // This code determines which activity is launched after this one
    public static final int CREATE_PROFILE = 0;
    public static final int EDIT_PROFILE = 1;
    public static final String NEXT_ACTIVITY = "nextActivity";
    FloatingActionButton fab;
    EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_display_name);

        fab = findViewById(R.id.fab);
        etName = findViewById(R.id.etName);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomUser curUser = CustomUser.queryForCurUser();
                curUser.setName(etName.getText().toString());
                curUser.saveInBackground();

                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    int nextActivity = extras.getInt(NEXT_ACTIVITY);
                    if (nextActivity == CREATE_PROFILE) {
                        Intent i = new Intent(SetDisplayNameActivity.this, AddPhotoActivity.class);
                        startActivity(i);
                    }
                    // TODO for edit display name once signed in

                }
            }
        });

    }
}