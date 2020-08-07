package com.livingtogether.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.livingtogether.adapters.MessagesAdapter;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.livingtogether.models.Group;
import com.livingtogether.models.Message;
import com.livingtogether.models.PinnedMessages;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    public static final String TAG = "ProfileFragment";
    public static final String CREATED_AT = "createdAt";

    private TextView tvName;
    private TextView tvGroup;
    private TextView tvOwed;
    private TextView tvLent;
    private ImageView ivProfile;
    private RecyclerView rvPinnedMessages;
    private MessagesAdapter adapter;
    private List<Message> allPinned;
    public CustomUser curUser;
    private SwipeRefreshLayout swipeContainer;
    private Spinner sortSpinner;
    private int positionOfCurItemSelected;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tvName);
        tvGroup = view.findViewById(R.id.tvGroup);
        tvLent = view.findViewById(R.id.tvLent);
        tvOwed = view.findViewById(R.id.tvOwed);
        curUser = CustomUser.queryForCurUser();
        builder = new AlertDialog.Builder(getContext());
        ivProfile = view.findViewById(R.id.ivProfile);
        positionOfCurItemSelected = 0;

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryMessages(positionOfCurItemSelected);
                NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();
                String strLent = defaultFormat.format(curUser.getLent());
                String strOwed = defaultFormat.format(curUser.getOwed());
                tvLent.setText("Your Group Owes You: " + strLent);
                tvOwed.setText("You Owe Others: " + strOwed);

            }
        });

        // setting refreshing colors
        swipeContainer.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.composeColor));

        if (curUser.getProfilePhoto() != null) {
            Glide.with(getContext())
                    .load(curUser.getProfilePhoto().getUrl())
                    .into(ivProfile);
        } else if (curUser.getIsFacebookUser()) {
            Glide
                    .with(getContext())
                    .load(curUser.getPhotoUrl())
                    .into(ivProfile);
        } else {
            ivProfile.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
        }

        tvName.setText(curUser.getName());
        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();
        String strLent = defaultFormat.format(curUser.getLent());
        String strOwed = defaultFormat.format(curUser.getOwed());
        tvLent.setText("Your Group Owes You: " + strLent);
        tvOwed.setText("You Owe Others: " + strOwed);

        Group group = null;
        try {
            group = curUser.getCurGroup().fetch();
            tvGroup.setText(group.getGroupName());
        } catch (Exception e) {
            e.printStackTrace();
            tvGroup.setText("");
            //TODO During Sign Up flow take the person to the group creation page
        }

        rvPinnedMessages = view.findViewById(R.id.rvPinnedMessages);
        allPinned = new ArrayList<>();
        adapter = new MessagesAdapter(getContext(), allPinned);

        // Click listeners must be set to prevent crashing as they are set in adaptor
        adapter.setOnItemClickListener(new MessagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                return;
            }
        });
        adapter.setOnItemLongClickListener(new MessagesAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                deleteDialogue(position);
            }
        });

        rvPinnedMessages.setAdapter(adapter);
        rvPinnedMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        sortSpinner = view.findViewById(R.id.sortSpinner);
        List<String> sortOptions = new ArrayList<>();
        sortOptions.add("All Sent Messages");
        sortOptions.add("All Saved Messages");
        sortOptions.add("Saved " + Message.MessageType.ANNOUNCEMENT.getName());
        sortOptions.add("Saved " + Message.MessageType.SHOPPING_LIST_ITEM.getName());
        sortOptions.add("Saved " + Message.MessageType.PURCHASE.getName());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.color_spinner_layout, sortOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setOnItemSelectedListener(this);

        queryPinnedMessages();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        positionOfCurItemSelected = position;
       queryMessages(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        queryMessagesProfile();
    }

    public void queryMessages(int spinnerPosition) {
        switch (spinnerPosition) {
            case 0:
                queryMessagesProfile();
                break;
            case 1:
                queryPinnedMessages();
                break;
            case 2:
                queryPinnedMessagesByType(Message.MessageType.ANNOUNCEMENT);
                break;
            case 3:
                queryPinnedMessagesByType(Message.MessageType.SHOPPING_LIST_ITEM);
                break;
            case 4:
                queryPinnedMessagesByType(Message.MessageType.PURCHASE);
                break;
            default:
                break;
        }
    }

    private void queryMessagesProfile() {
        ParseQuery query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_CUSTOM_USER);
        query.whereEqualTo(Message.KEY_CUSTOM_USER, curUser);
        query.setLimit(20);
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

    private void queryPinnedMessages() {
        ParseQuery query = ParseQuery.getQuery(PinnedMessages.class);
        query.include(PinnedMessages.KEY_MESSAGE);
        query.whereEqualTo(Message.KEY_CUSTOM_USER, curUser);
        query.addDescendingOrder(CREATED_AT);
        query.findInBackground(new FindCallback<PinnedMessages>() {
            @Override
            public void done(List<PinnedMessages> pinned, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with finding messages", e);
                    return;
                }
                adapter.clear();
                for (PinnedMessages pin : pinned) {
                    adapter.add(pin.getMessage());
                }
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void queryPinnedMessagesByType(Message.MessageType type) {
        ParseQuery query = ParseQuery.getQuery(PinnedMessages.class);
        query.include(PinnedMessages.KEY_MESSAGE);
        query.whereEqualTo(Message.KEY_CUSTOM_USER, curUser);
        query.whereEqualTo(Message.KEY_TYPE, type.toString());
        query.addDescendingOrder(CREATED_AT);
        query.findInBackground(new FindCallback<PinnedMessages>() {
            @Override
            public void done(List<PinnedMessages> pinned, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with finding messages", e);
                    return;
                }
                adapter.clear();
                for (PinnedMessages pin : pinned) {
                    adapter.add(pin.getMessage());
                }
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void deleteDialogue(final int position) {
        builder.setTitle("Remove this message from saved");
        Log.i(TAG, "deleteDialogue1");
        final Message message = allPinned.get(position);
        Log.i(TAG, "deleteDialogue2");
        if(message.getTypeAsEnum().equals(Message.MessageType.PURCHASE)) {
            Log.i(TAG, "deleteDialogue3");
            builder.setMessage("This will also remove the charge from your account!");
        }
        builder.setPositiveButton("REMOVE FROM SAVED", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "deleteDialogue4");
                deletePinned(message);
                if(message.getTypeAsEnum().equals(Message.MessageType.PURCHASE)) {
                    double cost = message.getCost();
                    message.getCustomUser().addLent(-1 * cost);
                    message.getCustomUser().saveInBackground();
                    curUser.addOwed(-1 * cost);
                    curUser.saveInBackground();
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        alertDialog = builder.create();
        Log.i(TAG, "deleteDialogue5");
        alertDialog.show();
    }

    private void deletePinned(Message message) {
        ParseQuery query = ParseQuery.getQuery(PinnedMessages.class);
        query.whereEqualTo(PinnedMessages.KEY_MESSAGE, message);
        query.whereEqualTo(PinnedMessages.KEY_CUSTOM_USER, curUser);
        query.findInBackground(new FindCallback<PinnedMessages>() {
            @Override
            public void done(List<PinnedMessages> pinned, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with querying for pin", e);
                    return;
                }
                for (PinnedMessages pin : pinned) {
                    pin.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error deleting pins", e);
                            }
                        }
                    });
                }
            }
        });
    }
}