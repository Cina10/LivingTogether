package com.livingtogether.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.livingtogether.MessagesAdapter;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.Message;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MessageBoardFragment extends Fragment {
    public static final String TAG = "MessageBoardFragment";

    private RecyclerView rvMessages;
    private MessagesAdapter adapter;
    private List<Message> allMessages;
    private SwipeRefreshLayout swipeContainer;

    public MessageBoardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryMessages();
            }
        });
        // setting refreshing colors
        swipeContainer.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.composeColor));



        rvMessages = view.findViewById(R.id.rvMessages);
        allMessages = new ArrayList<>();
        adapter = new MessagesAdapter(getContext(), allMessages);

        rvMessages.setAdapter(adapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        //layoutManager.canScrollVertically();
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rvMessages.setLayoutManager(layoutManager);
        queryMessages();
    }

    private void queryMessages() {
        ParseQuery query = ParseQuery.getQuery(Message.class);
        //query.setLimit(20);
        query.include(Message.KEY_CUSTOM_USER);
        query.setLimit(20);
        query.addDescendingOrder(Message.CREATED_AT);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with finding messages", e);
                    return;
                } else {
                    for (Message message : messages) {
                        Log.i(TAG, "Message: " + message.getTitle());
                    }
                    adapter.clear();
                    adapter.addAll(messages);
                    swipeContainer.setRefreshing(false);
                    Log.i(TAG, "Posts added");
                }
            }
        });
    }
}