package com.wl.android.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/*
 和风天气返回的数据中的daily_forecast部分的json数据包含一个数组，定义未来一天的单日天气对应的实体类：Forecast
 daily_forecast --> 未来几天的天气信息
 */

public class Forecast {

    public String date; // 未来某天日期

    @SerializedName("cond")
    public More more; // 未来某天天气概况

    @SerializedName("tmp")
    public Temperature temperature; // 未来某天气温

    public class More {

        @SerializedName("txt_d")
        public String info;
    }

    public class Temperature {

        public String max; // 最高气温

        public String min; // 最低气温
    }

}
