package com.wl.android.coolweather.gson;

/*
  和风天气返回的数据中的aqi部分的json数据对应的实体类：AQI -->当前空气质量的情况
 */

public class AQI {

    public AQICity city;

    public class AQICity {

        public String aqi; // aqi指数

        public String pm25; // pm2.5指数
    }
}
