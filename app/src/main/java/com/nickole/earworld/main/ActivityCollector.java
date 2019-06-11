package com.nickole.earworld.main;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shuzijie
 * @date 2019-05-11.
 */
public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for(int i=activities.size()-1;i>=0;i--) {
            if(!activities.get(i).isFinishing()) {
                activities.get(i).finish();
            }
        }
    }
}
