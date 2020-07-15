package com.example.livingtogether;

import android.app.Application;

import com.parse.Parse;
import com.parse.facebook.ParseFacebookUtils;

public class LivingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("livingtogether-chi") // should correspond to APP_ID env variable
                .clientKey("qwewehskshds")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://livingtogether-chi.herokuapp.com/parse").build());

        ParseFacebookUtils.initialize(this);
    }

}
