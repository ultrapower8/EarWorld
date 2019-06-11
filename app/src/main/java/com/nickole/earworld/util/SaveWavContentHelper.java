package com.nickole.earworld.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.nickole.earworld.main.MainApplication;

/**
 * @author shuzijie
 * @date 2019-06-07.
 */
public class SaveWavContentHelper {
    private static final String SP_NAME = "save_wav_content_mapping";
    private static SharedPreferences sp = MainApplication.getAppContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    public static void saveWavMessage(String wavName, String content) {
        if (wavName == null || content == null) {
            return;
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(wavName, content).apply();
    }

    public static String getWavContent(String wavName) {
        return sp.getString(wavName, "无内容");
    }
}
