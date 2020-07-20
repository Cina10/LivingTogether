package com.livingtogether.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String TAG = "Message";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    //public static final String KEY_MEDIA = "media";
    public static final String KEY_CUSTOM_USER = "customUser";
    public static final String KEY_BODY = "body";
    public static final String CREATED_AT = "createdAt";
    public static final String KEY_TYPE = "messageType";

    // Message types
    public static final int ANNOUNCEMENT_TYPE = 0;

    //for time calculations
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int YEAR_MILLIS = 365 * DAY_MILLIS;


    // Getters and Setters
    public String getTitle() {
        return getString(KEY_TITLE);
    }
    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public int getType() {
        return getInt(KEY_TYPE);
    }
    public void setType(int type) {
        put(KEY_TYPE, type);
    }

    public CustomUser getCustomUser() {
        return (CustomUser) get(KEY_CUSTOM_USER);
    }
    public void setCustomUser(CustomUser user) {
        put(KEY_CUSTOM_USER, user);
    }
    public String getBody() {
        return getString(KEY_BODY);
    }
    public void setBody(String body) {
        put(KEY_BODY, body);
    }

    // Time Calculation
    public String getRelativeTime() {
        long time = getCreatedAt().getTime();
        Log.i(TAG, "time: " + time);
        long now = System.currentTimeMillis();
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 2 * DAY_MILLIS) {
            return "1 day ago";
        } else if (diff < 7 * DAY_MILLIS) {
            return diff / DAY_MILLIS + " days ago";
        } else if (diff < YEAR_MILLIS) {
            return String.format("%tB %td", getCreatedAt());
        } else {
            // display time and date
            return String.format("%tB %td, %tY", getCreatedAt());
        }
    }


}