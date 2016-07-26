package com.example.zimmerman.coolweather.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by Zimmerman on 2016/7/23.
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
