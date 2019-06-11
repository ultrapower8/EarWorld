package com.nickole.earworld.transform;

import android.os.AsyncTask;

import com.nickole.earworld.api.LoadEngineApi;
import com.nickole.earworld.api.TextToSpeechApi;
import com.nickole.earworld.entity.TextInfo;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author shuzijie
 * @date 2019-05-13.
 */
public class VoiceSynthesisTask extends AsyncTask<Void, Integer, Boolean> {

    private TextInfo mTextInfo;
    private String voicePath;
//    private Queue<String> voicePathQueue;
    private ISynthesisListener mListener;
//    private int stepNum = 0;

    private boolean isUseWaitQueue = false;

    public VoiceSynthesisTask(ISynthesisListener listener) {
        this.mListener = listener;
    }

    public void setTextInfo(TextInfo textInfo) {
        this.mTextInfo = textInfo;
    }

    @Override
    protected void onPreExecute() {
        if (mListener != null) {
            mListener.beforeSynthesis();
        }
    }

    public boolean isUseWaitQueue() {
        return isUseWaitQueue;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (LoadEngineApi.isEngineLoadedFailed()) {
            return false;
        }
        CountDownLatch countDownLatch = LoadEngineApi.getCountDownLatch();
        if (countDownLatch != null) {
            try {
                countDownLatch.await(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (mTextInfo != null) {
//            if (mTextInfo.getTextList().size() > 10) {
//                isUseWaitQueue = true;
//                stepNum = (int) Math.ceil(mTextInfo.getTextList().size() / 10.0);
//                voicePathQueue = TextToSpeechApi.fetchSyntheticVoiceByWebSocket(mTextInfo, stepNum);
//            } else {
                voicePath = TextToSpeechApi.fetchSyntheticVoice(mTextInfo);
//            }
        }
//        if (isUseWaitQueue) {
//            return voicePathQueue != null;
//        } else {
            return voicePath != null;
//        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (mListener != null) {
            if (result) {
//                if (isUseWaitQueue) {
//                    mListener.onSuccess(voicePathQueue, stepNum);
//                } else {
                    mListener.onSuccess(voicePath);
//                }
            } else {
                mListener.onFailed();
            }
        }
    }
}
