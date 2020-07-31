package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.livingtogether.models.Group;
import com.livingtogether.models.Message;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class NewGroupActivity extends AppCompatActivity {
    public static final String TAG = "NewGroupActivity";
    EditText etGroupCode;
    EditText etName;
    Button btJoinGroup;
    Button btCreateGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        etGroupCode = findViewById(R.id.etGroupCode);
        etName = findViewById(R.id.etName);
        btJoinGroup = findViewById(R.id.btJoinGroup);
        btCreateGroup = findViewById(R.id.btCreateGroup);

        btJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO query for group that matches, set that group as your current group, add to your group list, add to group members array
                // remember to check if the user is already in the group
                joinGroup();
            }
        });

        btCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup();
            }
        });
    }

    private void joinGroup() {
        String name = etName.getText().toString();
        String groupCode = etGroupCode.getText().toString();
        if (name.isEmpty() && groupCode.isEmpty()) {
            Toast.makeText(this, "No group information entered", Toast.LENGTH_SHORT).show();
        } else {
            CustomUser curUser = CustomUser.queryForCurUser();
            Group group = Group.queryForGroup(name, groupCode);
            if (group == null) {
                Toast.makeText(this, "Group doesn't exist", Toast.LENGTH_SHORT).show();
            } else {
                curUser.setCurGroup(group);
                curUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent i = new Intent(NewGroupActivity.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
            }
        }
    }

    private void createGroup() {
        String name = etName.getText().toString();
        String groupCode = etGroupCode.getText().toString();
        if (name.isEmpty() && groupCode.isEmpty()) {
            Toast.makeText(this, "No group information entered", Toast.LENGTH_SHORT).show();
        } else {
            final CustomUser curUser = CustomUser.queryForCurUser();
            final Group group = Group.queryForGroup(name, groupCode);
            if (group == null) {
                final Group newGroup = new Group();
                newGroup.setGroupName(name);
                newGroup.setGroupCode(groupCode);
                newGroup.setAdmin(curUser);
                newGroup.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while creating group", e);
                            Toast.makeText(NewGroupActivity.this, "Error creating group", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NewGroupActivity.this, "Group joined", Toast.LENGTH_SHORT).show();
                            joinGroup();
                        }
                    }

                });
            } else {
                Toast.makeText(this, "Your group already exists", Toast.LENGTH_SHORT).show();
            }
        }

    }
}