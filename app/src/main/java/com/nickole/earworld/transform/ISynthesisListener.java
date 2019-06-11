package com.nickole.earworld.transform;

import java.util.Queue;

/**
 * @author shuzijie
 * @date 2019-05-13.
 */
public interface ISynthesisListener {
    void beforeSynthesis();

    void onSuccess(String voicePath);

    void onSuccess(Queue<String> voicePathQueue, int stepNum);

    void onFailed();
}
