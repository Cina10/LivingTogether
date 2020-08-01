package com.livingtogether.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

@ParseClassName("Like")
public class Like extends ParseObject {
    public static final String TAG = "Like";
    public static final String KEY_CUSTOM_USER = "customUser";
    public static final String KEY_MESSAGE = "message";

    public CustomUser getCustomUser() {
        return (CustomUser) get(KEY_CUSTOM_USER);
    }

    public void setCustomUser(CustomUser user) {
        put(KEY_CUSTOM_USER, user);
    }

    public Message getMessage() {
        return (Message) get(KEY_MESSAGE);
    }

    public void setMessage(Message message) {
        put(KEY_MESSAGE, message);
    }

    public static Like  queryIfLiked(CustomUser user)
    {
        ParseQuery query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo(Like.KEY_CUSTOM_USER, user);
        try {
            List<Like> likes = query.find();
            if(likes.isEmpty()) {
                return null;
            } else {
                return likes.get(0);
            }
        } catch (ParseException e) {
            Log.e(TAG, "Issue with querying for likes", e);
            return null;
        }
    }
}
