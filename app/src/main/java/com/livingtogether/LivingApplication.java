package com.livingtogether;

import android.app.Application;
import android.media.FaceDetector;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.livingtogether.models.Message;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.facebook.ParseFacebookUtils;

public class LivingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register Parse model
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(CustomUser.class);

        // values based on Heroku settings
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("livingtogether-chi")
                .clientKey("qwewehskshds")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://livingtogether-chi.herokuapp.com/parse").build());

        ParseFacebookUtils.initialize(this);
    }
}
