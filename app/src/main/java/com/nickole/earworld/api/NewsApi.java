package com.nickole.earworld.api;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nickole.earworld.entity.News;
import com.nickole.earworld.home.INotifyFetchNewsListener;
import com.nickole.earworld.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * @author shuzijie
 * @date 2019-05-15.
 */
public class NewsApi {
    private static final String NEWS_BASE_URL = "https://api01.idataapi.cn/news/toutiao";
//    private static final String NEWS_BASE_URL = "https://toutiao";
    private static final String API_KEY = "BtyW6XKgqmIo5FUGZrnpgsGkWBDLF0S9DOnXJQqTsAsz5977hothZPYWEu4G0Z4e";

    public static void fetchNewsList(final INotifyFetchNewsListener listener, final boolean isLoadMore, String nextPageToken) {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(NEWS_BASE_URL).newBuilder()
                .addQueryParameter("apikey", API_KEY)
                .addQueryParameter("kw", "科技");
        if (isLoadMore && !TextUtils.isEmpty(nextPageToken)) {
            httpUrlBuilder.addQueryParameter("pageToken", nextPageToken);
        }
        HttpUrl httpUrl = httpUrlBuilder.build();

        Log.d("earworldServer", httpUrl.toString());
        HttpUtil.sendGetAsyncRequest(httpUrl.toString(), new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("earworldServer", "Failed to connect to the news server: " + e);
                if (listener != null) {
                    if (isLoadMore) {
                        listener.onLoadMoreFailed(e.getMessage());
                    } else {
                        listener.onRefreshFailed(e.getMessage());
                    }
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200 && response.body() != null) {
                    String responseData = response.body().string();
                    Log.d("earworldServer", responseData);
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String message = jsonObject.optString("message");
                        if (!TextUtils.isEmpty(message)) {
                            if (isLoadMore) {
                                listener.onLoadMoreFailed(message);
                            } else {
                                listener.onRefreshFailed(message);
                            }
                            return;
                        }
                        boolean hasNext = jsonObject.getBoolean("hasNext");
                        String pageToken = jsonObject.optString("pageToken");
                        String jsonData = jsonObject.getString("data");
                        Gson gson = new Gson();
                        List<News> news = gson.fromJson(jsonData, new TypeToken<List<News>>() {
                        }.getType());
                        if (listener != null) {
                            if (isLoadMore) {
                                listener.onLoadMoreSuccess(news, hasNext, pageToken);
                            } else {
                                listener.onRefreshSuccess(news, hasNext, pageToken);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
