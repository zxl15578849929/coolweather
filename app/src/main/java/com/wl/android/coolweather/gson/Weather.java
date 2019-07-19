package com.wl.android.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
  总的天气实体类：Weather
 */

public class Weather {

    public String status; // 请求的状态，ok表示成功

    public Basic basic; // 城市的一些基本信息

    public AQI aqi; // 当前空气质量的情况

    public Now now; // 当前天气的信息

    public Suggestion suggestion; // 一些天气相关的生活建议信息

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList; //未来几天的天气信息，未来天气包含一个数组，单日天气对应Forecast
}
