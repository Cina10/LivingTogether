package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.livingtogether.livingtogether.R;

public class ComposeOptionsActivity extends AppCompatActivity implements View.OnClickListener {
    Button btAnnouncement;
    Button btShoppingList;
    Button btPurchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_options);
        btAnnouncement = findViewById(R.id.btAnnouncement);
        btShoppingList = findViewById(R.id.btShoppingList);
        btPurchase = findViewById(R.id.btPurchase);

        btAnnouncement.setOnClickListener(this);
        btShoppingList.setOnClickListener(this);
        btPurchase.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btAnnouncement:
                Intent i = new Intent(ComposeOptionsActivity.this, ComposeActivity.class);
                startActivity(i);
                break;
            case R.id.btShoppingList:
                i = new Intent(ComposeOptionsActivity.this, ShoppingItemComposeActivity.class);
                startActivity(i);
                break;
            case R.id.btPurchase:
                i = new Intent(ComposeOptionsActivity.this, RegisterItemListComposeActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}