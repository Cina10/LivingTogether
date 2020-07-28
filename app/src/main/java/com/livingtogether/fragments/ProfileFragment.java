package com.livingtogether.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.livingtogether.adaptors.MessagesAdapter;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.livingtogether.models.Message;
import com.livingtogether.models.PinnedMessages;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    public static final String CREATED_AT = "createdAt";

    private TextView tvName;
    private TextView tvGroup;
    private ImageView ivProfile;
    private RecyclerView rvPinnedMessages;
    private MessagesAdapter adapter;
    private List<Message> allPinned;
    private SwipeRefreshLayout swipeContainer;
    private CustomUser curUser;


    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPinnedMessages();
            }
        });

        // setting refreshing colors
        swipeContainer.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.composeColor));


        tvName = view.findViewById(R.id.tvName);
        curUser = CustomUser.queryForCurUser();

        ivProfile = view.findViewById(R.id.ivProfile);
        if (curUser.getProfilePhoto() != null) {
            Glide.with(getContext())
                    .load(curUser.getProfilePhoto().getUrl()).into(ivProfile);
        } else if (curUser.getIsFacebookUser()) {
            Glide.with(getContext())
                    .load(curUser.getPhotoUrl()).into(ivProfile);
        } else {
            ivProfile.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
        }

        tvName.setText(curUser.getName());
        tvGroup = view.findViewById(R.id.tvGroup);
        // TODO insert group name to header
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
                // TODO allow user to delete from pinned messages
            }
        });

        rvPinnedMessages.setAdapter(adapter);
        rvPinnedMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        queryPinnedMessages();

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
                // TODO fix issue when a post is deleted
                for (PinnedMessages pin : pinned) {
                    adapter.add(pin.getMessage());
                }
                swipeContainer.setRefreshing(false);
                Log.i(TAG, "Posts added");
            }
        });
    }

}