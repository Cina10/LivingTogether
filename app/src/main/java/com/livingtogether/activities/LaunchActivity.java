package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class LaunchActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "LaunchActivity";
    private static final int PHOTO_DIMENSIONS = 300;

    private Button btSignup;
    private Button btLogin;
    private Button fbLogin;
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // if already signed in
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        btLogin = findViewById(R.id.btLogin);
        btLogin.setOnClickListener(this);
        btSignup = findViewById(R.id.btSignup);
        btSignup.setOnClickListener(this);
        fbLogin = findViewById(R.id.login_button);
        fbLogin.setOnClickListener(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btLogin:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                login(username, password);
                break;
            case R.id.btSignup:
                goSignUpActivity();
                break;
            case R.id.login_button:
                signInFB();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void login(String username, String password) {
        Log.i(TAG, "login attempted");
        // nav to main act if user signed in correctly
        // use logInInBackground to provide better UX bc default happens on main thread of UI thread
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Toast.makeText(LaunchActivity.this, "Incorrect Login", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Issue with Login", e);
                    return;
                } else {
                    Intent i = new Intent(LaunchActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private void signInFB() {
        List<String> permissions = Arrays.asList("email", "public_profile", "user_birthday");
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
                    signUpFB();

                } else {
                    Log.i(TAG, "User logged in through Facebook!");
                    goMainActivity();
                }
            }
        });
    }

    private void signUpFB() {
        Toast.makeText(LaunchActivity.this, "New Account Created", Toast.LENGTH_SHORT).show();
        CustomUser customUser = new CustomUser();
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            String name = profile.getName();
            customUser.setName(name);
            customUser.setIsFacebookUser(true);
            customUser.setParseUser(ParseUser.getCurrentUser());
            String pictureUrl = profile.getProfilePictureUri(PHOTO_DIMENSIONS, PHOTO_DIMENSIONS).toString();
            customUser.setPhotoUrl(pictureUrl);
            customUser.saveInBackground();
            goGroupActivity();
        } else {
            Log.e(TAG, "Error retrieving Facebook profile");
        }
    }

    private void goMainActivity() {
        Intent i = new Intent(LaunchActivity.this, MainActivity.class);
        startActivity(i);
        finish();

    }

    private void goGroupActivity() {
        Intent i = new Intent(LaunchActivity.this, NewGroupActivity.class);
        startActivity(i);
    }


    private void goSignUpActivity() {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }
}