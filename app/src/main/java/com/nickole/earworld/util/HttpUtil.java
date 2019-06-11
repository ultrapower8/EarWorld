package com.nickole.earworld.util;

import com.nickole.earworld.api.Constant;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author shuzijie
 * @date 2019-05-13.
 */
public class HttpUtil {
    private static OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public static void sendPostAsyncRequest(String url, JSONObject jsonObject, okhttp3.Callback callback) {
        RequestBody requestBody = RequestBody.create(Constant.JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static Response sendPostSyncRequest(String url, JSONObject jsonObject) {
        RequestBody requestBody = RequestBody.create(Constant.JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendGetAsyncRequest(String url, okhttp3.Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
