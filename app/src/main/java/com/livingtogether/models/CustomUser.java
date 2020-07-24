package com.livingtogether.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("CustomUser")
public class CustomUser extends ParseObject {
    public static final String TAG = "CustomUser";
    public static final String KEY_USER = "user";
    public static final String KEY_PROFILE = "profilePhoto";
    public static final String KEY_NAME = "name";
    public static final String KEY_IS_FACEBOOK_USER = "isFacebookUser";
    public static final String KEY_PHOTO_URL = "photoUrl";
    public static final String KEY_CURRENT_GROUP = "curGroup";
    public static final String KEY_GROUP_LIST = "groups";
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

    public String getPhotoUrl() {
        return getString(KEY_PHOTO_URL);
    }

    public void setPhotoUrl(String photoUrl) {
        put(KEY_PHOTO_URL, photoUrl);
    }

    public boolean getIsFacebookUser() {
        return getBoolean(KEY_IS_FACEBOOK_USER);
    }

    public void setIsFacebookUser(boolean isFacebookUser) {
        put(KEY_IS_FACEBOOK_USER, isFacebookUser);
    }

    public ParseObject getCurGroup() {
        return getParseObject(KEY_CURRENT_GROUP);
    }

    public void setCurrentGroup(ParseObject currentGroup) {
        put(KEY_CURRENT_GROUP, currentGroup);
    }

    public ArrayList<Group> getGroups() {
        JSONArray groups = getJSONArray(KEY_GROUP_LIST);
        ArrayList<Group> ret = new ArrayList<>();
        if (groups != null) {
            for (int i = 0; i < groups.length(); i++) {

            }
        }
        return ret;
    }

    public void addGroup(Group group) {
        add(KEY_CURRENT_GROUP, group);
    }


    // TODO create methods to access balance list and todo list and to modify them
    public static CustomUser queryForCurUser(){
        ParseQuery query = ParseQuery.getQuery(CustomUser.class);

        query.whereEqualTo(CustomUser.KEY_USER, ParseUser.getCurrentUser());
        try {
            List<CustomUser> curUser = query.find();
            Log.i(TAG, curUser.get(0).getName());
            return curUser.get(0);
        } catch (ParseException e) {
            Log.e(TAG, "Issue with finding CustomUser", e);
            return null;
        }
    }

}
