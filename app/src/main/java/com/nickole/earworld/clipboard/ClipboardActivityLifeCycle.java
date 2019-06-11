package com.nickole.earworld.clipboard;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import static com.nickole.earworld.util.MonitorAppUtil.setIsBackGround;

public class ClipboardActivityLifeCycle implements Application.ActivityLifecycleCallbacks {

    private int mActivityCount = 0;
    private static boolean isToForeground = false;

    @Override
    public void onActivityStarted(Activity activity) {
        if (mActivityCount == 0) {
            isToForeground = true;
            setIsBackGround(false);
        }
        mActivityCount++;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        mActivityCount--;
        if (mActivityCount == 0) {
            setIsBackGround(true);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (isToForeground) {
//            //判断是否有缓存的语音
//            final String contentCache = ClipboardVoiceCacheHelper.getClipboardContent();
//            final String voicePathCache = ClipboardVoiceCacheHelper.getClipboardVoicePath();
//
//            ClipboardManager cm = (ClipboardManager) MainApplication.getAppContext()
//                    .getSystemService(CLIPBOARD_SERVICE);
//            String content = null;
//            if (cm.hasPrimaryClip() && cm.getPrimaryClip().getItemCount() > 0) {
//                //获取剪切板中最新的复制文本
//                content = cm.getPrimaryClip().getItemAt(0).getText().toString();
//                // 清除剪贴板
//                ClipData clip = ClipData.newPlainText("", "");
//                cm.setPrimaryClip(clip);
//            }
//
//            if (!TextUtils.isEmpty(content) || contentCache != null) {
//                final String textContent = content;
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                builder.setTitle("是否要朗读已复制的内容？")
//                        .setMessage(content)
//                        .setPositiveButton("朗读", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (TextUtils.isEmpty(textContent) ||
//                                        TextUtils.equals(contentCache, textContent)) {
//                                    initPlayer(voicePathCache);
//                                    playWav();
//                                    ClipboardVoiceCacheHelper.clearCache();
//                                } else {
//                                    startClipboardVoiceSynthesisTask(textContent);
//                                }
//                            }
//                        })
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                ClipboardVoiceCacheHelper.clearCache();
//                            }
//                        })
//                        .show();
//            }
            isToForeground = false;
        }
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    private void startClipboardVoiceSynthesisTask(String content) {
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
