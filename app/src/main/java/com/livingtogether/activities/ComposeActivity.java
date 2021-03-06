package com.livingtogether.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.livingtogether.models.Message;

import com.parse.Parse;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class ComposeActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "ComposeActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 12;
    private static final int GET_FROM_GALLERY_REQUEST_CODE = 42;
    protected static final String PHOTO_FILE_NAME = "photo.jpg";

    protected EditText etTitle;
    protected EditText etBody;
    protected ImageView ivPreview;
    protected Button btUpload;
    protected Button btTakePicture;
    protected Button btSubmit;
    protected File photoFile;
    protected ParseFile parseFile;
    protected ImageView ivExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etBody = (EditText) findViewById(R.id.etBody);
        btTakePicture = (Button) findViewById(R.id.btTakePicture);
        btTakePicture.setOnClickListener(this);
        btSubmit = (Button) findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(this);
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        btUpload = (Button) findViewById(R.id.btUpload);
        btUpload.setOnClickListener(this);
        ivExit = findViewById(R.id.ivExit);
        ivExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btSubmit:
                submit();
                break;
            case R.id.btTakePicture:
                launchCamera();
                break;
            case R.id.btUpload:
                retrievePhoto();
                break;
            case R.id.ivExit:
                Intent i = new Intent();
                i.putExtra(ComposeOptionsActivity.FINISH, ComposeOptionsActivity.FINISH_REQUEST_CODE);
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                Uri takenPhotoUri = Uri.fromFile(getPhotoFileUri(PHOTO_FILE_NAME));
                Bitmap rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                Bitmap resizedBitmap = scaleToFitWidth(rawTakenImage, 1500);
                resizedBitmap = rotateBitmap(resizedBitmap, 90);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                File resizedFile = getPhotoFileUri("resized_" + PHOTO_FILE_NAME);
                try {
                    resizedFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(resizedFile);
                    fos.write(bytes.toByteArray());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                photoFile = resizedFile;
                parseFile = new ParseFile(photoFile);

                ivPreview.setImageBitmap(resizedBitmap);
                ivPreview.setVisibility(View.VISIBLE);
            }
            if (requestCode == GET_FROM_GALLERY_REQUEST_CODE) {
                Uri imageUri = data.getData();
                ivPreview.setImageURI(imageUri);
                ivPreview.setVisibility(View.VISIBLE);

                Bitmap preview = ((BitmapDrawable) ivPreview.getDrawable()).getBitmap();
                if (preview != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    preview.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] image = stream.toByteArray();

                    parseFile =  new ParseFile(PHOTO_FILE_NAME, image);
                }
            }

        }
    }

    public void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(PHOTO_FILE_NAME);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(ComposeActivity.this, "com.codepath.fileProvider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // checks to make sure intent is safe to use, so the app doesn't crash
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap scaleToFitWidth(Bitmap b, int width) {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }

    private void retrievePhoto() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GET_FROM_GALLERY_REQUEST_CODE);
    }

    private void submit() {
        String title = etTitle.getText().toString();
        String body = etBody.getText().toString();
        Drawable preview = ivPreview.getDrawable();
        if (body.isEmpty() && title.isEmpty() && (preview == null)) {
            Toast.makeText(this, "No announcement entered!", Toast.LENGTH_SHORT).show();
        } else {
            Message message = new Message();
            message.setTitle(title);
            message.setBody(body);
            CustomUser curUser = CustomUser.queryForCurUser();
            message.setCustomUser(curUser);
            message.setType(Message.MessageType.ANNOUNCEMENT.toString());
            message.setLikes(0);
            message.setGroup(curUser.getCurGroup());
            if (preview != null)
                message.setImage(parseFile);
            message.saveInBackground();
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

}