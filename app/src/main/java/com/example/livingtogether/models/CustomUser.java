package com.example.livingtogether.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class CustomUser extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_PROFILE = "profilePhoto";
    public static final String KEY_NAME = "name";
    public static final String KEY_BALANCE = "balanceList";
    public static final String KEY_TODO = "toDoList";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setParseUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseFile getProfilePhoto() {
        return getParseFile(KEY_PROFILE);
    }
    public void setProfilePhoto(ParseFile profilePhoto) {
        put(KEY_PROFILE, profilePhoto);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    // TODO create methods to access balance list and todo list
}
