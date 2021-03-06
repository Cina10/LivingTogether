package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;

public class SetDisplayNameActivity extends AppCompatActivity {
    public static final int CREATE_PROFILE_ACTION_CODE = 0;
    public static final int EDIT_PROFILE_ACTION_CODE = 1;
    public static final String NEXT_ACTIVITY = "nextActivity";
    private Button btNext;
    private EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_display_name);
        btNext = findViewById(R.id.btNext);
        etName = findViewById(R.id.etName);

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomUser curUser = CustomUser.queryForCurUser();
                curUser.setName(etName.getText().toString());
                curUser.saveInBackground();

                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    int nextActivity = extras.getInt(NEXT_ACTIVITY);
                    if (nextActivity == CREATE_PROFILE_ACTION_CODE) {
                        Intent i = new Intent(SetDisplayNameActivity.this, AddPhotoActivity.class);
                        // TODO go to change profile photo activity
                        startActivity(i);
                    }
                    // TODO for edit display name once signed in
                }
            }
        });
    }
}