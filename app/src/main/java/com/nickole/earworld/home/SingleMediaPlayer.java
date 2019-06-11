package com.nickole.earworld.home;

import android.media.MediaPlayer;

/**
 * @author shuzijie
 * @date 2019-06-07.
 */
public class SingleMediaPlayer extends MediaPlayer {
    private static volatile SingleMediaPlayer player;

    public IPlayerStateListener getPlayerStateListener() {
        return iPlayerStateListener;
    }

    private IPlayerStateListener iPlayerStateListener;

    public void setPlayerStateListener(IPlayerStateListener iPlayerStateListener) {
        this.iPlayerStateListener = iPlayerStateListener;
    }

    private SingleMediaPlayer() {

    }

    public static SingleMediaPlayer getInstance() {
        if (player == null) {
            synchronized (SingleMediaPlayer.class) {
                if (player == null) {
                    player = new SingleMediaPlayer();
                }
            }
        }
        return player;
    }

    public static SingleMediaPlayer getPlayer() {
        return player;
    }

    public static void destoryPlayer() {
        player = null;
    }
}
