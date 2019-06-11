package com.nickole.earworld.event;

import com.nickole.earworld.entity.VoiceFile;

/**
 * @author shuzijie
 * @date 2019-06-04.
 */
public class PlayHistoryVoiceEvent {
    private VoiceFile mVoiceFile;

    public PlayHistoryVoiceEvent(VoiceFile voiceFile){
        mVoiceFile = voiceFile;
    }

    public VoiceFile getVoiceFile() {
        return mVoiceFile;
    }
}
