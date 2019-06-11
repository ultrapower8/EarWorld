package com.nickole.earworld.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shuzijie
 * @date 2019-06-03.
 */
public class TextUtil {
    public static boolean isChinese(String str){
        String regEx = "[\\u4e00-\\u9fa5]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
}
