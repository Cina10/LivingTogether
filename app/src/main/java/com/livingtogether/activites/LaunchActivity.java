package com.livingtogether.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.livingtogether.livingtogether.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;

import java.util.Arrays;
import java.util.List;

public class LaunchActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String EMAIL = "email";
    public static final String TAG = "Launch Activity";
    Button btSignup;
    Button btLogin;
    Button fbLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // if already signed in
        if (ParseUser.getCurrentUser() != null) {
            // TODO fix to main activity
            goMainActivity();
        }

        // finding the views and setting OnClickListeners
        btLogin = findViewById(R.id.btLogin);
        btLogin.setOnClickListener(this);
        btSignup = findViewById(R.id.btSignup);
        btSignup.setOnClickListener(this);
        fbLogin = findViewById(R.id.login_button);
        fbLogin.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                signInFB();
                break;
            case R.id.btLogin:
                goLoginActivity();
                break;
            case R.id.btSignup:
                // TODO
                break;
            default:
                break;
        }

    }

    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        //TODO make for result to finish() once you finish login activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void signInFB() {
        List<String> permissions = Arrays.asList(EMAIL);
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error occurred", e);
                } else {
                    Log.d(TAG, "logInWithReadPermissionsInBackground done");
                }

                if (parseUser == null) {
                    Log.i(TAG, "The user cancelled the Facebook login.");
                } else if (parseUser.isNew()) {
                    Log.i(TAG, "User signed up and logged in through Facebook!");
                    Toast.makeText(LaunchActivity.this, "New Account Created", Toast.LENGTH_SHORT).show();
                    goMainActivity();
                } else {
                    Log.i(TAG, "User logged in through Facebook!");
                    goMainActivity();

                }
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(LaunchActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}