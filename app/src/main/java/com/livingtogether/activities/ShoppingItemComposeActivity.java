package com.livingtogether.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.livingtogether.models.Message;

public class ShoppingItemComposeActivity extends ComposeActivity {
    public static final String TAG = "ShoppingItemComposeActivity";
    TextView tvPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        tvPage = findViewById(R.id.tvPage);
        etTitle = findViewById(R.id.etTitle);
        etBody = findViewById(R.id.etBody);
        btUpload = findViewById(R.id.btUpload);
        btTakePicture = findViewById(R.id.btTakePicture);
        btSubmit = findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        tvPage.setText("Add a Shopping List Item");
        btUpload.setVisibility(View.GONE);
        btTakePicture.setVisibility(View.GONE);
        etTitle.setHint("What item do you want?");
        etBody.setHint("Let your shopper know the details!");
    }

    private void submit() {
        String title = etTitle.getText().toString();
        String body = etBody.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(this, "No item entered!", Toast.LENGTH_SHORT).show();
        } else {
            Message item = new Message();
            item.setType(Message.MessageType.SHOPPING_LIST_ITEM.toString());
            item.setTitle(title);
            item.setBody(body);
            item.setQuantity(1);
            CustomUser curUser = CustomUser.queryForCurUser();
            item.setCustomUser(curUser);
            item.saveInBackground();
            etBody.setText("");
            etTitle.setText("");
            Intent i = new Intent(ShoppingItemComposeActivity.this, MainActivity.class);
            startActivity(i);
        }
    }
}