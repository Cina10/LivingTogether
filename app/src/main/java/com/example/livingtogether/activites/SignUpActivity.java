package com.example.livingtogether.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.livingtogether.R;

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = "SignupActivity";
    EditText etUsername;
    EditText etPassword;
    Button btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}