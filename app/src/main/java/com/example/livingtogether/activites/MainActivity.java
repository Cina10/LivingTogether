package com.example.livingtogether.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.livingtogether.MessagesAdapter;
import com.example.livingtogether.R;
import com.example.livingtogether.models.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
public static final String TAG = "MainActivity";
private FloatingActionButton fab;
private Toolbar toolbar;
private RecyclerView rvMessages;
private MessagesAdapter adapter;
private List<Message> allMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        fab = findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
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
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        queryMessages();

    }

    // retrieves messages
    private void queryMessages()
    {
        // Specify which class to query
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
//                    adapter.clear();
//                    adapter.addAll(posts);
//                    swipeContainer.setRefreshing(false);
                    Log.i(TAG, "Posts added");
                }
                allMessages.addAll(messages);
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}