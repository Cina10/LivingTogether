package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.livingtogether.livingtogether.R;


public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = "SettingsActivity";
    public static final int RESULT_NEW_GROUP = 4;

    ImageView curGroupArrow;
    ImageView allGroupsArrow;
    CardView curGroupBt;
    CardView allGroupsBt;
    LinearLayout groupSettingsWrapper;
    LinearLayout allGroupsWrapper;
    Button btAddGroup;
    // TODO Profile settings bt functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        curGroupArrow = findViewById(R.id.curGroupArrow);
        allGroupsArrow = findViewById(R.id.allGroupsArrow);
        groupSettingsWrapper = findViewById(R.id.groupSettingWrapper);
        allGroupsWrapper = findViewById(R.id.allGroupsWrapper);
        curGroupBt = findViewById(R.id.curGroupBt);
        allGroupsBt = findViewById(R.id.allGroupsBt);
        btAddGroup = findViewById(R.id.btAddGroup);

        curGroupBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCurGroup();
            }
        });

        allGroupsBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAllGroups();
            }
        });

        btAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, NewGroupActivity.class);
                startActivityForResult(i, RESULT_NEW_GROUP);
                // TODO onActivityResult add the resulting group to the list of Groups
            }
        });

    }

    private void onCurGroup() {
        if (groupSettingsWrapper.getVisibility() == View.VISIBLE) {
            groupSettingsWrapper.setVisibility(View.GONE);
            curGroupArrow.setImageResource(R.drawable.ic_action_foward);
        } else {
            groupSettingsWrapper.setVisibility(View.VISIBLE);
            curGroupArrow.setImageResource(R.drawable.ic_action_down);
        }
    }

    private void onAllGroups() {
        if (allGroupsWrapper.getVisibility() == View.VISIBLE) {
            allGroupsWrapper.setVisibility(View.GONE);
            allGroupsArrow.setImageResource(R.drawable.ic_action_foward);
        } else {
            allGroupsWrapper.setVisibility(View.VISIBLE);
            allGroupsArrow.setImageResource(R.drawable.ic_action_down);
        }
    }

}