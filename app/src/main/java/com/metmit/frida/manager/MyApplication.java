package com.metmit.frida.manager;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

public class MyApplication extends Application {

    private volatile static WeakReference<Context> contextWeakReference = null;

    @Override
    public void onCreate() {
        super.onCreate();
        contextWeakReference = new WeakReference<>(getApplicationContext());
    }

    public static Context getContext() {
        return contextWeakReference.get();
    }
}