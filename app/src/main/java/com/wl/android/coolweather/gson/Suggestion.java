package com.wl.android.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/*
  和风天气返回的数据中的suggestion部分的json数据对应的实体类：Suggestion --> 一些天气相关的生活建议信息
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort; // 舒适度

    @SerializedName("cw")
    public CarWash carWash; // 洗车指数

    public Sport sport; // 运动建议

    public class Comfort {

        @SerializedName("txt")
        public String info;
    }

    public class CarWash {

        @SerializedName("txt")
        public String info;
    }

    public class Sport {

        @SerializedName("txt")
        public String info;
    }
}
