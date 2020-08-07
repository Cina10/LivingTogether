package com.livingtogether.models;

import android.util.Log;

import com.livingtogether.activities.MainActivity;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ParseClassName("Message")
public class Message extends ParseObject implements Comparable<Message> {
    public static final String TAG = "Message";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_CUSTOM_USER = "customUser";
    public static final String KEY_BODY = "body";
    public static final String KEY_TYPE = "messageType";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_GROUP = "group";
    public static final String KEY_COST = "cost";

    // For time calculations
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int YEAR_MILLIS = 365 * DAY_MILLIS;

    private int curRelevancy;


    public enum MessageType {
        ANNOUNCEMENT("Announcements", 1),
        TODO("To Do List", 2),
        PURCHASE("Purchases", 0),
        SHOPPING_LIST_ITEM("Shopping List", 2);

        private final String name;
        private final int priority;

        MessageType(String name, int priority) {
            this.name = name;
            this.priority = priority;
        }

        public String getName() {
            return name;
        }

        public int getPriority() {
            return priority;
        }
    }

    @Override
    public int compareTo(Message other) {
        long diffInMills = getCreatedAt().getTime() - other.getCreatedAt().getTime();
        return -1 * (int) diffInMills;
    }

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

    public String getType() {
        return getString(KEY_TYPE);
    }

    public MessageType getTypeAsEnum() {
        String type = getString(KEY_TYPE);
        if (type.equals(MessageType.PURCHASE.toString())) {
            return MessageType.PURCHASE;
        }
        if (type.equals(MessageType.SHOPPING_LIST_ITEM.toString())) {
            return MessageType.SHOPPING_LIST_ITEM;
        } else {
            return MessageType.ANNOUNCEMENT;
        }
    }

    public void setType(String type) {
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

    public int getLikes() {
        return getInt(KEY_LIKES);
    }

    public void setLikes(int i) {
        put(KEY_LIKES, i);
    }

    public void incrementLikes() {
        put(KEY_LIKES, getInt(KEY_LIKES) + 1);
    }

    public void decrementLikes() {
        put(KEY_LIKES, getInt(KEY_LIKES) - 1);
    }

    public Group getGroup() {
        return (Group) get(KEY_GROUP);
    }

    public void setGroup(Group group) {
        put(KEY_GROUP, group);
    }

    public double getCost() {
        return getDouble(KEY_COST);
    }

    public void setCost(double cost) {
        put(KEY_COST, cost);
    }

    public String getRelativeTime() {
        long time = getCreatedAt().getTime();
        long now = System.currentTimeMillis();
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 2 * HOUR_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 2 * DAY_MILLIS) {
            return "1 day ago";
        } else if (diff < 7 * DAY_MILLIS) {
            return diff / DAY_MILLIS + " days ago";
        } else if (diff < YEAR_MILLIS) {
            DateFormat df = new SimpleDateFormat("MMMM dd");
            return df.format(getCreatedAt());
        } else {
            DateFormat df = new SimpleDateFormat("MMMM dd, YYYY");
            return df.format(getCreatedAt());
        }
    }

    public static List<Message> queryForMessageType(MessageType type) {
        ParseQuery query = ParseQuery.getQuery(Message.class);
        query.whereEqualTo(Message.KEY_TYPE, type.toString());
        try {
            List<Message> messages = query.find();
            return messages;
        } catch (ParseException e) {
            Log.e(TAG, "Issue with queryForMessageType()", e);
            return null;
        }
    }


    public static void assignRelevancy(List<Message> messages) {
        for (Message message : messages) {
            int relevancyScore = 0;
            long time = message.getCreatedAt().getTime();
            long now = System.currentTimeMillis();
            long diffInMills = now - time;
            long diffInDays = TimeUnit.DAYS.convert(diffInMills, TimeUnit.MILLISECONDS);
            relevancyScore += 3 * diffInDays;
            int likes = message.getLikes();
            relevancyScore -= 2 * likes;
            if (Like.queryIfLiked(message, MainActivity.getCurUser()) != null) {
                relevancyScore -= 2;
            }
            Message.MessageType type = message.getTypeAsEnum();
            if (type == MessageType.SHOPPING_LIST_ITEM) {
                relevancyScore -= 3 * (type.getPriority() * (diffInDays+1));
            } else {
                relevancyScore -= 4 * type.getPriority();
            }
            message.curRelevancy = relevancyScore;
        }
    }

    public int getCurRelevancy() {
        return curRelevancy;
    }
}
