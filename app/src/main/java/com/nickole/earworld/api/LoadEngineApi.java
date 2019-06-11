package com.nickole.earworld.api;

import android.util.Log;

import com.nickole.earworld.util.HttpUtil;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author shuzijie
 * @date 2019-05-13.
 */
public class LoadEngineApi {
    private static final String LOAD_ENGINE_URL = Constant.API_URL_PREFIX + "/loadmodel";
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public static Boolean isEngineLoadedFailed() {
        return isEngineLoadedFailed;
    }

    public static void setIsEngineLoadedFailed(Boolean isEngineLoadedFailed) {
        LoadEngineApi.isEngineLoadedFailed = isEngineLoadedFailed;
    }

    private static Boolean isEngineLoadedFailed = false;

    public static void loadEngine() {
        HttpUtil.sendGetAsyncRequest(LOAD_ENGINE_URL, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("earworldServer", "Failed to load the TTS engine");
                isEngineLoadedFailed = true;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200 && response.body() != null) {
                    String responseData = response.body().string();
                    isEngineLoadedFailed = false;
                    countDownLatch.countDown();
                    Log.d("earworldServer", responseData);
                }
            }
        });
    }
}
