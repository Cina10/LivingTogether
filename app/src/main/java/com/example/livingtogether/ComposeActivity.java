package com.example.livingtogether;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.livingtogether.models.CustomUser;
import com.example.livingtogether.models.Message;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.boltsinternal.Task;

import java.util.ArrayList;
import java.util.List;

public class ComposeActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "ComposeActivity";
    public static final int ANNOUNCEMENT_TYPE = 0;
    EditText etTitle;
    EditText etBody;
    ImageView ivPreview;
    Button btUpload;
    Button btTakePicture;
    Button btSubmit;

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

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btSubmit:
                //show preview first
                submit();
                break;
            case R.id.btTakePicture:
                // TODO
                break;
            case R.id.btUpload:
                // TODO
                break;
            default:
                break;
        }

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
            Log.i(TAG, curUser.getName());
            message.setCustomUser(curUser);
            message.setType(ANNOUNCEMENT_TYPE);
            //message.setImage();
            message.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving", e);
                        Toast.makeText(ComposeActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, "Post saved!");
                        etBody.setText("");
                        etTitle.setText("");
                    }
                }
            });
        }
    }

}