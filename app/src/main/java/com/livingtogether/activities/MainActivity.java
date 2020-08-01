package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.livingtogether.fragments.MessageBoardFragment;
import com.livingtogether.fragments.ProfileFragment;
import com.livingtogether.livingtogether.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.livingtogether.models.CustomUser;
import com.livingtogether.models.Group;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private static CustomUser curUser;
    private FloatingActionButton floatingbt;
    private Toolbar toolbar;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        curUser = CustomUser.queryForCurUser();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new MessageBoardFragment();
        ft.replace(R.id.flContainer, fragment);
        ft.commit();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        floatingbt = findViewById(R.id.floating_action_button);
        floatingbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent composeIntent = new Intent(MainActivity.this, ComposeOptionsActivity.class);
                startActivity(composeIntent);
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
        final FragmentManager fragmentManager = getSupportFragmentManager();

        switch (item.getItemId()) {
            case R.id.actionSettings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.actionMessageBoard:
                fragment = new MessageBoardFragment();
                break;
            case R.id.actionProfile:
                fragment = new ProfileFragment();
                break;
            case R.id.actionLogout:
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser == null) {
                    Intent launchActivityIntent = new Intent(MainActivity.this, LaunchActivity.class);
                    launchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(launchActivityIntent );
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        return true;
    }

    public static CustomUser getCurUser() {
        return curUser;
    }
}