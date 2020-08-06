package com.livingtogether.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_CUSTOM_USER = "customUser";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TEXT = "text";

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

    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setText(String text) {
        put(KEY_TEXT, text);
    }
}
