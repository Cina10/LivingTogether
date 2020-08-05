package com.livingtogether.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.livingtogether.adapters.MessagesAdapter;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.Message;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class RegisterItemListComposeActivity extends AppCompatActivity {
    public static final String TAG = "RegisterItemListComposeActivity";
    private RecyclerView rvShoppingList;
    private MessagesAdapter adapter;
    private List<Message> allItems;
    private ImageView ivExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_item_list_compose);

        rvShoppingList = findViewById(R.id.rvShoppingList);
        allItems = new ArrayList<>();
        adapter = new MessagesAdapter(this, allItems);
        adapter.setOnItemClickListener(new MessagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Message message = allItems.get(position);
                Intent intent = new Intent(RegisterItemListComposeActivity.this, ReceiptComposeActivity.class);
                intent.putExtra(Message.class.getSimpleName(), Parcels.wrap(message));
                startActivityForResult(intent, ComposeOptionsActivity.FINISH_REQUEST_CODE);
            }
        });

        ivExit = findViewById(R.id.ivExit);
        ivExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapseFinish();
            }
        });

        // This is required, otherwise crashes on long click
        adapter.setOnItemLongClickListener(new MessagesAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                return;
            }
        });

        rvShoppingList.setAdapter(adapter);
        rvShoppingList.setLayoutManager(new LinearLayoutManager(this));

        queryForShoppingList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ComposeOptionsActivity.FINISH_REQUEST_CODE) {
            int finishCode = data.getExtras().getInt(ComposeOptionsActivity.FINISH);
            if (finishCode == ComposeOptionsActivity.FINISH_REQUEST_CODE ) {
                collapseFinish();
            }
        }
    }

    // finishes with an intent that will signal parent activities to also finish
    public void collapseFinish() {
        Intent i = new Intent();
        i.putExtra(ComposeOptionsActivity.FINISH, ComposeOptionsActivity.FINISH_REQUEST_CODE);
        setResult(RESULT_OK, i);
        finish();
    }

    public void queryForShoppingList() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.whereEqualTo(Message.KEY_TYPE, Message.MessageType.SHOPPING_LIST_ITEM.toString());
        query.include(Message.KEY_CUSTOM_USER);
        // TODO order based on how many likes
        query.findInBackground(new FindCallback<Message>() {
            @SuppressLint("LongLogTag")
            @Override
            public void done(List<Message> items, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with finding shopping items", e);
                    return;
                }
                allItems.addAll(items);
                adapter.notifyDataSetChanged();
            }
        });
    }
}