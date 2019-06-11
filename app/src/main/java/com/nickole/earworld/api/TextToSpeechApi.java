package com.nickole.earworld.api;

import android.text.TextUtils;
import android.util.Log;

import com.nickole.earworld.entity.TextInfo;
import com.nickole.earworld.main.MainApplication;
import com.nickole.earworld.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * @author shuzijie
 * @date 2019-05-13.
 */
public class TextToSpeechApi {
    private static final String TEXT_TO_SPEECH_URL = Constant.API_URL_PREFIX + "/text2speech";
    private static final String DEFAULT_FILE_NAME = "synthetic_voice_result.wav";

    public static String fetchSyntheticVoice(TextInfo textInfo) {
        String voicePath = null;
        JSONObject textInfoJson = new JSONObject();
        JSONArray textListJson = new JSONArray();
        for (String text : textInfo.getTextList()) {
            textListJson.put(text);
        }
        try {
            textInfoJson.put("id", textInfo.getId());
            textInfoJson.put("type", textInfo.getType());
            textInfoJson.put("standstill_time", textInfo.getStandstillTime());
            textInfoJson.put("text_list", textListJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response response = HttpUtil.sendPostSyncRequest(TEXT_TO_SPEECH_URL, textInfoJson);
        if (response != null && response.code() == 200 && response.body() != null) {
            String dirPath = MainApplication.getAppContext().getCacheDir().getAbsolutePath();
            InputStream input = null;
            FileOutputStream fos = null;
            try {
                input = response.body().byteStream();
                File file = new File(dirPath + "/" + getHeaderFileName(response));
                fos = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[2048];
                while ((len = input.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                voicePath = file.getAbsolutePath();
            } catch (IOException ignored) {
                Log.d("earworldServer", "Failed to fetch synthetic voice");
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException ignored) {

                }
            }
        }
        return voicePath;
    }

//    public static Queue<String> fetchSyntheticVoiceByWebSocket(final TextInfo textInfo, final int stepNum) {
//        final Queue<String> waitPlayQueue = new ConcurrentLinkedQueue<>();
//
//        JSONObject textInfoJson = new JSONObject();
//        JSONArray textListJson = new JSONArray();
//        for (String text : textInfo.getTextList()) {
//            textListJson.put(text);
//        }
//        try {
//            textInfoJson.put("id", textInfo.getId());
//            textInfoJson.put("type", textInfo.getType());
//            textInfoJson.put("standstill_time", textInfo.getStandstillTime());
//            textInfoJson.put("text_list", textListJson);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
//        RequestBody requestBody = RequestBody.create(Constant.JSON, textInfoJson.toString());
//        Request request = new Request.Builder()
//                .url(TEXT_TO_SPEECH_URL)
//                .post(requestBody)
//                .build();
//        WebSocket webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
//            WebSocket webSocket = null;
//            int index = 0;
//
//            @Override
//            public void onOpen(final WebSocket webSocket, final Response response) {
//                this.webSocket = webSocket;
//            }
//
//            @Override
//            public void onMessage(final WebSocket webSocket, String text) {
//
//            }
//
//            @Override
//            public void onMessage(final WebSocket webSocket, ByteString bytes) {
//                String dirPath = MainApplication.getAppContext().getCacheDir().getAbsolutePath();
//                BufferedOutputStream bos = null;
//                FileOutputStream fos = null;
//                try {
//                    File file = new File(dirPath + "/" + textInfo.getId() + "_" + index++);
//                    fos = new FileOutputStream(file);
//                    bos = new BufferedOutputStream(fos);
//                    bytes.write(bos);
//                    //加入播放队列
//                    waitPlayQueue.add(file.getAbsolutePath());
//                    if (stepNum == index) {
//                        //完成所有子语音片段的合成
//                        webSocket.close(1000, "Finish" + stepNum + " files reception.");
//                    }
//                } catch (Exception e) {
//                    Log.d("earworldServer", "Failed to fetch synthetic voice");
//                } finally {
//                    if (bos != null) {
//                        try {
//                            bos.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (fos != null) {
//                        try {
//                            fos.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//            //webSocket关闭时，关闭线程池
//            @Override
//            public void onClosed(WebSocket webSocket, int code, String reason) {
//                super.onClosed(webSocket, code, reason);
//            }
//        });
//
//        return waitPlayQueue;
//    }

    private static String getHeaderFileName(Response response) {
        String dispositionHeader = response.header("Content-Disposition");
        if (!TextUtils.isEmpty(dispositionHeader)) {
            dispositionHeader.replace("attachment;filename=", "");
            dispositionHeader.replace("filename*=utf-8", "");
            String[] strings = dispositionHeader.split("; ");
            if (strings.length > 1) {
                dispositionHeader = strings[1].replace("filename=", "");
                dispositionHeader = dispositionHeader.replace("\"", "");
                return dispositionHeader;
            }
        }
        return DEFAULT_FILE_NAME;
    }
}
