package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.livingtogether.adaptors.MessagesAdapter;
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
                startActivity(intent);
            }
        });

        // Necessary to setOnItemLongClick to prevent crashing because MessageBoard uses it
        adapter.setOnItemLongClickListener(new MessagesAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                Message message = allItems.get(position);
                Intent intent = new Intent(RegisterItemListComposeActivity.this, ReceiptComposeActivity.class);
                intent.putExtra(Message.class.getSimpleName(), Parcels.wrap(message));
                startActivity(intent);
            }
        });

        rvShoppingList.setAdapter(adapter);
        rvShoppingList.setLayoutManager(new LinearLayoutManager(this));

        queryForShoppingList();
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