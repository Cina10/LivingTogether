package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.parse.ParseFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AddPhotoActivity extends AppCompatActivity {
    public static final String TAG = "AddPhotoActivity";
    public static final int GET_FROM_GALLERY = 42;

    private FloatingActionButton floatingbt;
    private Button btUpload;
    private ImageView ivProfile;
    private File photoFile;
    private String photoFileName = "profile.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        floatingbt = findViewById(R.id.fab);
        floatingbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable preview = ivProfile.getDrawable();
                if (preview != null) {
                    CustomUser curUser = CustomUser.queryForCurUser();
                    curUser.setProfilePhoto(new ParseFile(photoFile));
                    curUser.saveInBackground();
                }
                Intent i = new Intent(AddPhotoActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        btUpload = findViewById(R.id.btUpload);
        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPhoto();
            }
        });
        ivProfile = findViewById(R.id.ivProfile);
    }

    private void uploadPhoto() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                Toast.makeText(this, "Bitmap retrieved", Toast.LENGTH_SHORT).show();
                ivProfile.setImageBitmap(bitmap);
                ivProfile.setVisibility(View.VISIBLE);

            } catch (FileNotFoundException e) {
                Log.e(TAG, "FileNotFound");
            } catch (IOException e) {
                Log.e(TAG, "IOException");
            }
        }
    }

//    private void resize(Bitmap bitmap) {
//        Bitmap resizedBitmap = ComposeActivity.scaleToFitWidth(bitmap, 1500);
//
//        // Configure byte output stream
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        // Compress the image further
//        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
//        // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
//        File resizedFile = getPhotoFileUri("resized_" + photoFileName);
//        try {
//            resizedFile.createNewFile();
//            FileOutputStream fos = new FileOutputStream(resizedFile);
//            // Write the bytes of the bitmap to file
//            fos.write(bytes.toByteArray());
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        photoFile = resizedFile;
//
//        ivProfile.setImageBitmap(resizedBitmap);
//        Toast.makeText(this, "Image View", Toast.LENGTH_SHORT).show();
//        ivProfile.setVisibility(View.VISIBLE);
//    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }
}