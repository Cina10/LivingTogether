package com.livingtogether.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.livingtogether.models.Message;

public class ShoppingItemComposeActivity extends ComposeActivity {
    public static final String TAG = "ShoppingItemComposeActivity";
    private TextView tvPage;
    private LinearLayout messageWrapper;

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
        messageWrapper = findViewById(R.id.messageWrapper);
        ivExit = findViewById(R.id.ivExit);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        ivExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra(ComposeOptionsActivity.FINISH, ComposeOptionsActivity.FINISH_REQUEST_CODE);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        tvPage.setText("Add a Shopping List Item");
        btUpload.setVisibility(View.GONE);
        btTakePicture.setVisibility(View.GONE);
        etTitle.setHint("What item do you want?");
        etBody.setHint("Let your shopper know the details!");
        messageWrapper.setBackgroundTintList(getResources().getColorStateList(R.color.shopping_list));
    }

    private void submit() {
        String title = etTitle.getText().toString();
        String body = etBody.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(this, "No item entered!", Toast.LENGTH_SHORT).show();
        } else {
            CustomUser curUser = CustomUser.queryForCurUser();
            Message item = new Message();
            item.setType(Message.MessageType.SHOPPING_LIST_ITEM.toString());
            item.setTitle(title);
            item.setBody(body);
            item.setGroup(curUser.getCurGroup());
            item.setCustomUser(curUser);
            item.setLikes(0);
            item.saveInBackground();
            etBody.setText("");
            etTitle.setText("");
            Intent i = new Intent(ShoppingItemComposeActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }
}