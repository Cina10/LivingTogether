package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.livingtogether.fragments.MessageBoardFragment;
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
    private Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = new MessageBoardFragment();;
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        floatingbt = findViewById(R.id.floating_action_button);
        floatingbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ComposeActivity.class);
                startActivity(i);
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
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.actionProfile:
                // TODO go to profile
                return true;
            case R.id.actionLogout:
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser == null) {
                    i = new Intent(MainActivity.this, LaunchActivity.class);
                    startActivity(i);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}