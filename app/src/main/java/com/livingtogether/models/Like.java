package com.livingtogether.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Like")
public class Like extends ParseObject {
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
}
