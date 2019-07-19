package com.wl.android.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/*
 * 服务器交互工具类：HttpUtil
 */

public class HttpUtil {

    /**
     * http请求方法
     *
     * @param address 请求地址
     * @param callback 回调处理服务器响应
     */
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
