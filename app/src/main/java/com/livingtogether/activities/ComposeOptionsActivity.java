package com.livingtogether.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.livingtogether.livingtogether.R;

public class ComposeOptionsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int FINISH_REQUEST_CODE = 21;
    public static final String FINISH = "finish";
    Button btAnnouncement;
    Button btShoppingList;
    Button btPurchase;
    ImageView ivExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_options);
        btAnnouncement = findViewById(R.id.btAnnouncement);
        btShoppingList = findViewById(R.id.btShoppingList);
        btPurchase = findViewById(R.id.btPurchase);
        ivExit = findViewById(R.id.ivExit);

        btAnnouncement.setOnClickListener(this);
        btShoppingList.setOnClickListener(this);
        btPurchase.setOnClickListener(this);
        ivExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btAnnouncement:
                Intent i = new Intent(ComposeOptionsActivity.this, ComposeActivity.class);
                startActivityForResult(i, FINISH_REQUEST_CODE);
                break;
            case R.id.btShoppingList:
                i = new Intent(ComposeOptionsActivity.this, ShoppingItemComposeActivity.class);
                startActivityForResult(i, FINISH_REQUEST_CODE);
                break;
            case R.id.btPurchase:
                i = new Intent(ComposeOptionsActivity.this, RegisterItemListComposeActivity.class);
                startActivityForResult(i, FINISH_REQUEST_CODE);
                break;
            case R.id.ivExit:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == FINISH_REQUEST_CODE) {
            int finishCode = data.getExtras().getInt(FINISH);
            if (finishCode == FINISH_REQUEST_CODE ) {
                finishAfterTransition();
            }
        }
    }
}