package com.wl.android.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/*
  和风天气返回的数据中的basic部分的json数据对应的实体类：Basic --> 城市的一些基本信息
 */

public class Basic {

    /*
      使用@SerializedName注解来将对象里的属性跟json里字段对应值匹配起来
     */
    @SerializedName("city")
    public String cityName; // 城市名

    @SerializedName("id")
    public String weatherId; // 天气id

    public Update update; // 更新时间

    public class Update {

        @SerializedName("loc")
        public String updateTime;
    }
}
