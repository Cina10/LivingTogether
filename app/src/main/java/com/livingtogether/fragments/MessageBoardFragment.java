package com.livingtogether.fragments;

import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.livingtogether.activities.MainActivity;
import com.livingtogether.activities.MessageDetailActivity;
import com.livingtogether.adapters.MessagesAdapter;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.livingtogether.models.Group;
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
    public static final int REQUEST_CODE = 42;
    public static final String CREATED_AT = "createdAt";
    public static final String PRIORITY = "priority";
    public static final String MESSAGE = "message";
    public static final String POSITION = "position";

    private RecyclerView rvMessages;
    private MessagesAdapter adapter;
    private List<Message> allMessages;
    private SwipeRefreshLayout swipeContainer;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private Spinner sortSpinner;
    private int positionOfCurItemSelected;

    public MessageBoardFragment() {
    }

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
                queryMessages(positionOfCurItemSelected);
            }
        });

        // setting refreshing colors
        swipeContainer.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.composeColor));

        builder = new AlertDialog.Builder(getContext());
        rvMessages = view.findViewById(R.id.rvMessages);
        allMessages = new ArrayList<>();
        positionOfCurItemSelected = 0;
        adapter = new MessagesAdapter(getContext(), allMessages);
        adapter.setOnItemLongClickListener(new MessagesAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                saveDialogue(position);
            }
        });
        adapter.setOnItemClickListener(new MessagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Message message = allMessages.get(position);
                Intent intent = new Intent(getActivity(), MessageDetailActivity.class);
                intent.putExtra(MESSAGE, Parcels.wrap(message));
                intent.putExtra(POSITION, position);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        rvMessages.setAdapter(adapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rvMessages.setLayoutManager(layoutManager);

        sortSpinner = view.findViewById(R.id.sortSpinner);
        List<String> sortOptions = new ArrayList<>();
        sortOptions.add("Priority");
        sortOptions.add("Time Created");
        sortOptions.add(Message.MessageType.ANNOUNCEMENT.getName());
        sortOptions.add(Message.MessageType.SHOPPING_LIST_ITEM.getName());
        sortOptions.add(Message.MessageType.PURCHASE.getName());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.color_spinner_layout, sortOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setOnItemSelectedListener(this);
        queryMessages(PRIORITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Message message = Parcels.unwrap(data.getParcelableExtra(MESSAGE));
            int position = data.getExtras().getInt(POSITION);
            allMessages.add(position, message);
            allMessages.remove(position + 1);
            adapter.notifyItemChanged(position);
        }
    }

    // Method for the spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        positionOfCurItemSelected = position;
        queryMessages(position);
    }

    // Method for the spinner
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        queryMessages(positionOfCurItemSelected);
    }

    private void saveDialogue(final int position) {
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
                        if (e == null) {
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

    public void queryMessages(int spinnerPosition) {
        switch (spinnerPosition) {
            case 0:
                queryMessages(PRIORITY);
                break;
            case 1:
                queryMessages(CREATED_AT);
                break;
            case 2:
                queryForMessageType(Message.MessageType.ANNOUNCEMENT);
                break;
            case 3:
                queryForMessageType(Message.MessageType.SHOPPING_LIST_ITEM);
                break;
            case 4:
                queryForMessageType(Message.MessageType.PURCHASE);
                break;
            default:
                break;
        }
    }

    private void queryMessages(final String sortMode) {
        ParseQuery query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_CUSTOM_USER);

        CustomUser curUser = CustomUser.queryForCurUser();
        Group group = curUser.getCurGroup();
        query.whereEqualTo(Message.KEY_GROUP, group);
        query.setLimit(20);
        query.addDescendingOrder(CREATED_AT);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with finding messages", e);
                    return;
                } else {
                    if (sortMode.equals(PRIORITY)) {
                        Collections.sort(messages, new Comparator<Message>() {
                            @Override
                            public int compare(Message a, Message b) {
                                int relevancyScore = 0;
                                long diffInMills = a.getCreatedAt().getTime() - b.getCreatedAt().getTime();
                                long diff = TimeUnit.DAYS.convert(diffInMills, TimeUnit.MILLISECONDS);
                                relevancyScore -= 3 * diff;
                                int diffInLikes = a.getLikes() - b.getLikes();
                                relevancyScore += 2 * diffInLikes;
                                if (Like.queryIfLiked(a, MainActivity.getCurUser()) != null) {
                                    relevancyScore -= 2;
                                }
                                if (Like.queryIfLiked(b, MainActivity.getCurUser()) != null) {
                                    relevancyScore += 2;
                                }
                                int diffInPriority;
                                diffInPriority = a.getTypeAsEnum().getPriority() - b.getTypeAsEnum().getPriority();
                                relevancyScore -= 6 * diffInPriority;
                                // TODO make more complex: different scoring like urgency, that goes higher the more days have passed instead of lower like announcements
                                return relevancyScore;
                            }
                        });
                    }
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