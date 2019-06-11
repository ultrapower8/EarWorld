package com.nickole.earworld.util;

/**
 * @author shuzijie
 * @date 2019-05-23.
 */
public class MonitorAppUtil {
    /**
     * 前后台标志位
     */
    private static boolean sInBackgroud = true;

    /**
     * 前后台切换时调用
     *
     * @param status, true表示进入后台
     */
    public static void setIsBackGround(boolean status) {
        sInBackgroud = status;
    }

    public static boolean isBackground() {
        return sInBackgroud;
    }
}
