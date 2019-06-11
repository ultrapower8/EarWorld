package com.nickole.earworld.history;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nickole.earworld.R;
import com.nickole.earworld.entity.VoiceFile;
import com.nickole.earworld.event.PlayHistoryVoiceEvent;
import com.nickole.earworld.main.MainApplication;
import com.taishi.library.Indicator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nickole on 2018/3/2.
 */

public class HistoryVoiceAdapter extends RecyclerView.Adapter {

    private List<VoiceFile> mVoiceList = new ArrayList<>();
    private int currentNewsPosition = -1;
    private Activity mActivity;

    public HistoryVoiceAdapter(Activity activity) {
        mActivity = activity;
    }

    public List<VoiceFile> getVoiceFileList() {
        return mVoiceList;
    }

    public void setVoiceFileList(List<VoiceFile> voiceFiles) {
        this.mVoiceList = voiceFiles;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_voice, parent, false);
            final HistoryVoiceViewHolder baseHolder = new HistoryVoiceViewHolder(view);

            baseHolder.returnPlayButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int position = baseHolder.getAdapterPosition();
                    if (currentNewsPosition != position) {
                        if (ContextCompat.checkSelfPermission(MainApplication.getAppContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(mActivity, new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            }, 1);
                        } else {
                            baseHolder.returnPlayButton.setVisibility(View.GONE);
                            baseHolder.voiceIndicator.setVisibility(View.VISIBLE);
                            baseHolder.returnPlayButton.setClickable(false);
                            //播放
                            VoiceFile voiceFile = mVoiceList.get(position);
                            EventBus.getDefault().post(new PlayHistoryVoiceEvent(voiceFile));
//
                            //更新上一个播放的按钮样式
                            int lastPosition = currentNewsPosition;
                            currentNewsPosition = position;
                            if (lastPosition > -1) {
                                notifyItemChanged(lastPosition);
                            }
                        }
                    }
                }
            });
            return baseHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VoiceFile voiceFile = mVoiceList.get(position);
        HistoryVoiceViewHolder baseHolder = (HistoryVoiceViewHolder) holder;
        baseHolder.fileTitleTextView.setText(voiceFile.getContent());
        baseHolder.fileCreateDateTextView.setText(voiceFile.getCreateDate());
        baseHolder.fileTotalTimeTextView.setText(voiceFile.getTotalTime());
        baseHolder.fileSizeTextView.setText(voiceFile.getFileSize());
        if (position == mVoiceList.size() -1) {
            baseHolder.voicePartingLine.setVisibility(View.GONE);
        } else {
            baseHolder.voicePartingLine.setVisibility(View.VISIBLE);
        }

        if (currentNewsPosition != position) {
            baseHolder.returnPlayButton.setVisibility(View.VISIBLE);
            baseHolder.voiceIndicator.setVisibility(View.GONE);
            baseHolder.returnPlayButton.setClickable(true);
        }
    }

    @Override
    public int getItemCount() {
        if (mVoiceList != null) {
            return mVoiceList.size();
        }
        return 0;
    }

    static class HistoryVoiceViewHolder extends RecyclerView.ViewHolder {
        TextView fileTotalTimeTextView;
        TextView fileTitleTextView;
        TextView fileCreateDateTextView;
        TextView fileSizeTextView;
        ImageView returnPlayButton;
        Indicator voiceIndicator;
        View voicePartingLine;

        HistoryVoiceViewHolder(View view) {
            super(view);

            fileTitleTextView = view.findViewById(R.id.file_title);
            fileCreateDateTextView = view.findViewById(R.id.file_create_date);
            fileTotalTimeTextView = view.findViewById(R.id.file_total_time);
            fileSizeTextView = view.findViewById(R.id.file_size);
            returnPlayButton = view.findViewById(R.id.return_play_button);
            voiceIndicator = view.findViewById(R.id.voice_indicator);
            voicePartingLine = view.findViewById(R.id.item_voice_parting_line);
        }
    }
}
