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
    private static final String EMAIL = "email";

    private Button btSignup;
    private Button btLogin;
    private Button fbLogin;

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
                goSignupActivity();
                break;
            default:
                break;
        }
    }

    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        // TODO make for result to finish() once you finish login activity
    }

    private void goSignupActivity() {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
        // TODO make for result to finish() once you finish login activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
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
                    signUpFBUser();

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

    private void signUpFBUser() {
        Toast.makeText(LaunchActivity.this, "New Account Created", Toast.LENGTH_SHORT).show();
        CustomUser customUser = new CustomUser();
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            String name = profile.getName();
            customUser.setName(name);
            customUser.setIsFacebookUser(true);
            customUser.setParseUser(ParseUser.getCurrentUser());
            String pictureUrl = profile.getProfilePictureUri(300, 300).toString();
            customUser.setPhotoUrl(pictureUrl);
            customUser.saveInBackground();
            goMainActivity();
        } else {
            Log.e(TAG, "Error retrieving Facebook profile");
        }
    }
}