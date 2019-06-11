package com.nickole.earworld.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author shuzijie
 * @date 2019-05-16.
 */
public class DateTimeUtil {
    private static int MILLION_SECOND_EVERY_DAY = 86400000;

    public static String DateTime2ShowStr(String dateTime) {
        dateTime = dateTime.replaceAll("T", " ");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long interval = System.currentTimeMillis() - c.getTimeInMillis();
        if (interval < MILLION_SECOND_EVERY_DAY) {
            long hours = (interval % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            if (hours > 0) {
                return hours + "小时前";
            }
            long minutes = (interval % (1000 * 60 * 60)) / (1000 * 60);
            if (minutes > 0) {
                return minutes + "分钟前";
            }
            long seconds = (interval % (1000 * 60)) / 1000;
            if (seconds > 0) {
                return seconds + "秒前";
            }
        }
        Date date = new Date();
        date.setTime(c.getTimeInMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
        return simpleDateFormat.format(date);
    }

    public static String formatTime(int length) {
        Date date = new Date(length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.US);
        return simpleDateFormat.format(date);
    }
}
