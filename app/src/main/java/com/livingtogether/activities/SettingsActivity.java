package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.livingtogether.adapters.UserAdapter;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = "SettingsActivity";
    public static final int RESULT_NEW_GROUP = 4;

    private ImageView ivExit;
    private List<CustomUser> allMembers;
    private UserAdapter adapter;

    private ImageView curGroupArrow;
    private CardView curGroupBt;
    private LinearLayout groupSettingsWrapper;
    private RecyclerView rvMembers;

    private ImageView allGroupsArrow;
    private CardView allGroupsBt;
    private LinearLayout allGroupsWrapper;
    private Button btAddGroup;
    // TODO Profile settings bt functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ivExit = findViewById(R.id.ivExit);
        curGroupArrow = findViewById(R.id.curGroupArrow);
        groupSettingsWrapper = findViewById(R.id.groupSettingWrapper);
        curGroupBt = findViewById(R.id.curGroupBt);
        rvMembers = findViewById(R.id.rvMembers);

        allGroupsArrow = findViewById(R.id.allGroupsArrow);
        allGroupsWrapper = findViewById(R.id.allGroupsWrapper);
        allGroupsBt = findViewById(R.id.allGroupsBt);
        btAddGroup = findViewById(R.id.btAddGroup);

        ivExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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

        allMembers = new ArrayList<>();
        adapter = new UserAdapter(this, allMembers);
        rvMembers.setAdapter(adapter);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        queryForMembers();
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

    public void queryForMembers() {
        ParseQuery<CustomUser> query = ParseQuery.getQuery(CustomUser.class);
        query.whereEqualTo(CustomUser.KEY_CURRENT_GROUP, MainActivity.getCurUser().getCurGroup());
        query.findInBackground(new FindCallback<CustomUser>(){
            @Override
            public void done(List<CustomUser> members, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "User query issue", e);
                }
                allMembers.addAll(members);
                adapter.notifyDataSetChanged();
            }
        });
    }
}