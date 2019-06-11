package com.nickole.earworld.api;


import okhttp3.MediaType;

/**
 * @author shuzijie
 * @date 2019-05-13.
 */
public class Constant {
    private Constant() {
    }

    public static final String API_URL_PREFIX = "https://9a9098bb.ngrok.io";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
}
