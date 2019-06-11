package com.nickole.earworld.util;

import com.nickole.earworld.soundtouch.SoundTouch;

/**
 * @author shuzijie
 * @date 2019-05-23.
 */

public class AudioUtil {
    /**
     * 调节语音语速
     */
    public static boolean changeVoiceSpeed(int speed, String inFileName, String outFileName) {
        float tempo = 0;
        switch (speed) {
            case 0:
                tempo = -50;
                break;
            case 1:
                tempo = 0;
                break;
            case 2:
                tempo = 50;
                break;
            default:
        }
        SoundTouch st = new SoundTouch();
        st.setTempo(tempo);
        int res = st.processFile(inFileName, outFileName);
        return res == 0;
    }

    /**
     * 调节语音音调
     */
    public static boolean changeVoiceTone(int tone, String inFileName, String outFileName) {
        float pitch = 0;
        switch (tone) {
            case 0:
                pitch = -5;
                break;
            case 1:
                pitch = 0;
                break;
            case 2:
                pitch = 5;
                break;
            default:
        }
        SoundTouch st = new SoundTouch();
        st.setPitchSemiTones(pitch);
        int res = st.processFile(inFileName, outFileName);
        return res == 0;
    }
}


