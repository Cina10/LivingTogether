package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.card.MaterialCardView;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;

public class AddPhotoActivity extends AppCompatActivity {
    private static final int GET_FROM_GALLERY_REQUEST_CODE = 42;
    private static final String PHOTO_FILE_NAME = "profile.jpg";

    private Button btNext;
    private Button btUpload;
    private ImageView ivProfile;
    private MaterialCardView photoWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        btNext = findViewById(R.id.btNext);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPhoto();
            }
        });

        btUpload = findViewById(R.id.btUpload);
        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrievePhoto();
            }
        });

        ivProfile = findViewById(R.id.ivProfile);
        photoWrapper = findViewById(R.id.photoWrapper);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_FROM_GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            ivProfile.setImageURI(imageUri);
            photoWrapper.setVisibility(View.VISIBLE);
        }
    }

    private void retrievePhoto() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GET_FROM_GALLERY_REQUEST_CODE);
    }

    private void uploadPhoto() {
        Bitmap preview = ((BitmapDrawable) ivProfile.getDrawable()).getBitmap();
        if (preview != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            preview.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();

            ParseFile photoFile = new ParseFile(PHOTO_FILE_NAME, image);
            CustomUser curUser = CustomUser.queryForCurUser();
            curUser.setProfilePhoto(photoFile);
            curUser.saveInBackground();
        }
        Intent i = new Intent(AddPhotoActivity.this, NewGroupActivity.class);
        startActivity(i);
    }
}