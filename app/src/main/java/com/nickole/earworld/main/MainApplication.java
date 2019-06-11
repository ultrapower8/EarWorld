package com.nickole.earworld.main;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nickole.earworld.clipboard.ClipboardActivityLifeCycle;

/**
 * @author shuzijie
 * @date 2019-05-13.
 */
public class MainApplication extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        setAppContext(this);
        Fresco.initialize(this);

        registerActivityLifecycleCallbacks(new ClipboardActivityLifeCycle());
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void setAppContext(Context context) {
        appContext = context;
    }
}
