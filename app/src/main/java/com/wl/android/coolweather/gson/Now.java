package com.wl.android.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/*
  和风天气返回的数据中的now部分的json数据对应的实体类：Now --> 当前天气的信息
 */

public class Now {

    @SerializedName("tmp")
    public String temperature; // 当前气温

    @SerializedName("cond")
    public More more; // 当前天气概况

    public class More {

        @SerializedName("txt")
        public String info;
    }
}
