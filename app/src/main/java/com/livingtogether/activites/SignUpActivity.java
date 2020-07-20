package com.livingtogether.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.livingtogether.livingtogether.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = "SignupActivity";
    TextView tvPage;
    EditText etUsername;
    EditText etPassword;
    Button btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvPage = findViewById(R.id.tvPage);
        tvPage.setText("Sign Up");
        etUsername = findViewById(R.id.etUsername);
        etUsername.setHint("Create a username");
        etPassword = findViewById(R.id.etPassword);
        etPassword.setHint("Choose a password");
        btLogin = findViewById(R.id.btLogin);
        btLogin.setText("Create a profile!");
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick login");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                signUp(username, password);
            }
        });
    }

    private void signUp(final String username, final String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    login(username,password);

                } else {
                    Toast.makeText(SignUpActivity.this, "Sorry, there was an issue with signing up", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                Intent i = new Intent(SignUpActivity.this, CreateProfileActivity.class);
                startActivity(i);
                finish();

            }
        });
    }
}
