package com.livingtogether.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("PinnedMessages")
public class PinnedMessages extends ParseObject {
    public static final String KEY_CUSTOM_USER = "customUser";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TYPE = "messageType";

    public CustomUser getCustomUser() {
        return (CustomUser) get(KEY_CUSTOM_USER);
    }

    public void setCustomUser(CustomUser user) {
        put(KEY_CUSTOM_USER, user);
    }

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setType(String type) {
        put(KEY_TYPE, type);
    }

    public Message getMessage() {
        return (Message) get(KEY_MESSAGE);
    }

    public void setMessage(Message message) {
        put(KEY_MESSAGE, message);
    }

}
