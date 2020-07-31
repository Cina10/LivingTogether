package com.livingtogether.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@ParseClassName("Group")
public class Group extends ParseObject {
    public static final String TAG = "Group";
    public static final String KEY_ADMIN = "admin";
    public static final String KEY_GROUP_NAME = "groupName";
    public static final String KEY_GROUP_CODE = "groupCode";

    public ParseUser getAdmin() {
        return getParseUser(KEY_ADMIN);
    }

    public void setAdmin(CustomUser admin) {
        put(KEY_ADMIN, admin);
    }

    public String getGroupName() {
        return getString(KEY_GROUP_NAME);
    }

    public void setGroupName(String groupName) {
        put(KEY_GROUP_NAME, groupName);
    }

    public String getGroupCode() {
        return getString(KEY_GROUP_CODE);
    }

    public void setGroupCode(String groupCode) {
        put(KEY_GROUP_CODE, groupCode);
    }

    public static Group queryForGroup(String name, String code) {
        ParseQuery query = ParseQuery.getQuery(Group.class);
        query.whereEqualTo(Group.KEY_GROUP_NAME, name);
        query.whereEqualTo(Group.KEY_GROUP_CODE, code);
        try {
            List<Group> groups = query.find();
            return groups.get(0);
        } catch (Exception e) {
            // if there is no such group returns null
            return null;
        }
    }
}

