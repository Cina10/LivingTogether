package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.livingtogether.MessagesAdapter;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
public static final String TAG = "MainActivity";

private FloatingActionButton floatingbt;
private Toolbar toolbar;
private RecyclerView rvMessages;
private MessagesAdapter adapter;
private List<Message> allMessages;
private SwipeRefreshLayout swipeContainer;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               queryMessages();
            }
        });
        // setting refreshing colors
        swipeContainer.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.composeColor));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        floatingbt= findViewById(R.id.floating_action_button);
        floatingbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ComposeActivity.class);
                startActivity(i);
            }
        });

        rvMessages = findViewById(R.id.rvMessages);
        allMessages = new ArrayList<>();
        adapter = new MessagesAdapter(this, allMessages);

        rvMessages.setAdapter(adapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        //layoutManager.canScrollVertically();
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rvMessages.setLayoutManager(layoutManager);
        queryMessages();
    }

    private void queryMessages() {
        ParseQuery query = ParseQuery.getQuery(Message.class);
        //query.setLimit(20);
        query.include(Message.KEY_CUSTOM_USER);
        query.setLimit(20);
        query.addDescendingOrder(Message.CREATED_AT);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with finding messages", e);
                    return;
                } else {
                    for (Message message : messages) {
                        Log.i(TAG, "Message: " + message.getTitle());
                    }
                    adapter.clear();
                    adapter.addAll(messages);
                    swipeContainer.setRefreshing(false);
                    Log.i(TAG, "Posts added");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionGroupSettings:
                Intent i = new Intent()
                return true;
            case R.id.actionProfile:
                // TODO go to profile settings
                return true;
            case R.id.actionLogout:
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser == null) {
                    Intent i = new Intent(MainActivity.this, LaunchActivity.class);
                    startActivity(i);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}