package com.nickole.earworld.api;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.nickole.earworld.main.MainApplication;
import com.nickole.earworld.util.HttpUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author shuzijie
 * @date 2019-05-13.
 */
public class ConnectEngineApi {
    private static final String CONNECT_ENGINE_URL = Constant.API_URL_PREFIX + "/connect";

    public static void connectEngine(Activity activity) {
        final WeakReference<Activity> weakActivity = new WeakReference<>(activity);
        HttpUtil.sendGetAsyncRequest(CONNECT_ENGINE_URL, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("earworldServer", "Failed to connect to the server: " + e);
                LoadEngineApi.setIsEngineLoadedFailed(true);
                final Activity activity = weakActivity.get();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "连接服务器异常", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200 && response.body() != null) {
                    String responseData = response.body().string();
                    Log.d("earworldServer", responseData);
                    //加载tts模型
                    LoadEngineApi.loadEngine();
                }
            }
        });
    }
}
