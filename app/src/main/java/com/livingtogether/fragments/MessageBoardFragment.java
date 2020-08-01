package com.livingtogether.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.livingtogether.activities.MainActivity;
import com.livingtogether.activities.MessageDetailActivity;
import com.livingtogether.adaptors.MessagesAdapter;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.livingtogether.models.Like;
import com.livingtogether.models.Message;
import com.livingtogether.models.PinnedMessages;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MessageBoardFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "MessageBoardFragment";
    public static final String CREATED_AT = "createdAt";

    private RecyclerView rvMessages;
    private MessagesAdapter adapter;
    private List<Message> allMessages;
    private SwipeRefreshLayout swipeContainer;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private Spinner sortSpinner;

    public MessageBoardFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        builder =  new AlertDialog.Builder(getContext());
        rvMessages = view.findViewById(R.id.rvMessages);
        allMessages = new ArrayList<>();
        adapter = new MessagesAdapter(getContext(), allMessages);
        adapter.setOnItemLongClickListener(new MessagesAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                pinDialogue(position);
            }
        });
        adapter.setOnItemClickListener(new MessagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Message message = allMessages.get(position);
                Intent intent = new Intent(getActivity(), MessageDetailActivity.class);
                intent.putExtra(Message.class.getSimpleName(), Parcels.wrap(message));
                startActivity(intent);
                // TODO also set onClick to bring to details view
                // must keep as place holder so it doesn't crash
            }
        });

        rvMessages.setAdapter(adapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rvMessages.setLayoutManager(layoutManager);

        sortSpinner = view.findViewById(R.id.sortSpinner);
        List<String> sortOptions = new ArrayList<>();
        sortOptions.add("All Messages");
        sortOptions.add(Message.MessageType.ANNOUNCEMENT.getName());
        sortOptions.add(Message.MessageType.SHOPPING_LIST_ITEM.getName());
        sortOptions.add(Message.MessageType.PURCHASE.getName());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sortOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setOnItemSelectedListener(this);
        queryMessages();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {
            case 0:
                queryMessages();
                break;
            case 1:
                queryForMessageType(Message.MessageType.ANNOUNCEMENT);
                break;
            case 2:
                queryForMessageType(Message.MessageType.SHOPPING_LIST_ITEM);
                break;
            case 3:
                queryForMessageType(Message.MessageType.PURCHASE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        queryMessages();
    }

    private void pinDialogue(final int position) {
        builder.setTitle("Save this message?");
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PinnedMessages pinned = new PinnedMessages();
                Message message = allMessages.get(position);
                pinned.setMessage(message);
                pinned.setCustomUser(CustomUser.queryForCurUser());
                pinned.setType(message.getType());
                pinned.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            // TODO replace toast with snackbar, check if message has already been pinned
                            Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Error saving pinned message", e);
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void queryMessages() {
        ParseQuery query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_CUSTOM_USER);
        query.whereEqualTo(Message.KEY_GROUP, MainActivity.getCurUser().getCurGroup());
        query.setLimit(20);
        query.addDescendingOrder(CREATED_AT);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with finding messages", e);
                    return;
                } else {
                    Collections.sort(messages, new Comparator<Message>() {
                        @Override
                        public int compare(Message a, Message b) {
                            int relevancyScore = 0;
                            long diffInMills = a.getCreatedAt().getTime() - b.getCreatedAt().getTime();
                            long diff = TimeUnit.DAYS.convert(diffInMills, TimeUnit.MILLISECONDS);
                            relevancyScore -= 3 * diff;
                            int diffInLikes = a.getLikes() - b.getLikes();
                            relevancyScore += 2 * diffInLikes;
                            if (Like.queryIfLiked(MainActivity.getCurUser()) != null) {
                                relevancyScore -= 2;
                            }
                            if (Like.queryIfLiked(MainActivity.getCurUser()) != null) {
                                relevancyScore += 2;
                            }
                            int diffInPriority;
                            diffInPriority = a.getTypeAsEnum().getPriority() - b.getTypeAsEnum().getPriority();
                            relevancyScore -= 6 * diffInPriority;
                            // TODO make more complex: different scoring like urgency, that goes higher the more days have passed instead of lower like announcements
                            return relevancyScore;
                        }
                    });
                    adapter.clear();
                    adapter.addAll(messages);
                    swipeContainer.setRefreshing(false);
                }
            }
        });
    }

    private void queryForMessageType(Message.MessageType type) {
        ParseQuery query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_CUSTOM_USER);
        query.setLimit(20);
        query.whereEqualTo(Message.KEY_TYPE, type.toString());
        query.whereEqualTo(Message.KEY_GROUP, MainActivity.getCurUser().getCurGroup());
        query.addDescendingOrder(CREATED_AT);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with finding messages", e);
                    return;
                } else {
                    adapter.clear();
                    adapter.addAll(messages);
                    swipeContainer.setRefreshing(false);
                }
            }
        });
    }
}