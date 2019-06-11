package com.nickole.earworld.home;

/**
 * @author shuzijie
 * @date 2019-06-06.
 */
public interface IPlayerStateListener {
    void onWavCompletion();

    void onPlayWav();

    void onPauseWav();
}
