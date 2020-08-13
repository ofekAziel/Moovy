package com.example.moovy.application;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
