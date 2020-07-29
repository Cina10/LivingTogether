package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.livingtogether.livingtogether.R;

import org.w3c.dom.Text;

public class MessageDetailActivity extends AppCompatActivity {
    ImageView ivProfile;
    TextView tvTitle;
    TextView tvBody;
    ImageView ivMedia;
    TextView tvTime;
    TextView tvLikeDescription;
    ImageView ivLike;
    RecyclerView rvComments;
    EditText etComment;
    ImageButton btSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        ivProfile = findViewById(R.id.ivProfile);
        tvTitle = findViewById(R.id.tvTitle);
        tvBody = findViewById(R.id.tvBody);
        ivMedia = findViewById(R.id.ivMedia);
        tvTime = findViewById(R.id.tvTime);
        tvLikeDescription = findViewById(R.id.tvLikeDescription);
        ivLike = findViewById(R.id.ivLike);
        rvComments = findViewById(R.id.rvComments);
        etComment = findViewById(R.id.etComment);
        btSend = findViewById(R.id.btSend);
    }
}