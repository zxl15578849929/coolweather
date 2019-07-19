package com.wl.android.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wl.android.coolweather.gson.Forecast;
import com.wl.android.coolweather.gson.Weather;
import com.wl.android.coolweather.service.AutoUpdateService;
import com.wl.android.coolweather.util.HttpUtil;
import com.wl.android.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ImageView mBingPicImageView;

    public DrawerLayout mDrawerLayout; // 滑动菜单

    private Button mNavButton; // 自定义标题栏上的滑动菜单按钮

    public SwipeRefreshLayout mSwipeRefreshLayout; // 下拉刷新

    private String mWeatherId; // 天气id

    private ScrollView mWeatherLayoutScrollView;

    private TextView mTitleCityTextView; // 城市名 Basic.cityName

    private TextView mTitleUpdateTimeTextView; // 更新时间 Basic.update.updateTime

    private TextView mDegreeTextView; // 当前气温 Now.temperature

    private TextView mWeatherInfoTextView; // 当前天气概况 Now.more.info

    private LinearLayout mForecastLinearLayout;

    private TextView mAqiTextView; // aqi指数 AQI.city.aqi

    private TextView mPm25TextView; // pm2.5指数 AQI.city.pm25

    private TextView mComfortTextView; // 舒适度 Suggestion.comfort.info

    private TextView mCarWashTextView; // 洗车指数 Suggestion.carWash.info

    private TextView mSportTextView; // 运动建议 Suggestion.sport.info

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置背景图与状态栏融合到一起
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            // 清除 FLAG_TRANSLUCENT_STATUS Flag(透明状态栏).
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            // 添加 FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS Flag(绘制系统栏).
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        // 初始化各控件
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavButton = (Button) findViewById(R.id.nav_button);
        mNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START); // 打开滑动菜单
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary); // 设置下拉刷新进度条颜色

        mBingPicImageView = (ImageView) findViewById(R.id.bing_pic_img);
        mWeatherLayoutScrollView = (ScrollView) findViewById(R.id.weather_layout);
        mTitleCityTextView = (TextView) findViewById(R.id.title_city);
        mTitleUpdateTimeTextView = (TextView) findViewById(R.id.title_update_time);
        mDegreeTextView = (TextView) findViewById(R.id.degree_text);
        mWeatherInfoTextView = (TextView) findViewById(R.id.weather_info_text);
        mForecastLinearLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        mAqiTextView = (TextView) findViewById(R.id.aqi_text);
        mPm25TextView = (TextView) findViewById(R.id.pm25_text);
        mComfortTextView = (TextView) findViewById(R.id.comfort_text);
        mCarWashTextView = (TextView) findViewById(R.id.car_wash_text);
        mSportTextView = (TextView) findViewById(R.id.sport_text);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            // 无缓存去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            mWeatherLayoutScrollView.setVisibility(View.INVISIBLE); // 请求数据时隐藏ScrollView
            requestWeather(mWeatherId);
        }

        // 下拉刷新监听器
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        String bingPic = preferences.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(mBingPicImageView);
        } else {
            loadBingPic();
        }


    }

    /**
     * 根据天气id请求城市天气信息
     *
     * @param weatherId 天气id
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId
                + "&key=445d68805e1a432995137583cea72dfb";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            // 从服务器中成功获取到天气信息，先缓存到SharedPreferences
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather", responseText);
                            editor.apply();

                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        mSwipeRefreshLayout.setRefreshing(false); // 刷新结束，隐藏进度条
                    }
                });
            }
        });
        loadBingPic();
    }

    /**
     * 处理并显示Weather实体类中的数据
     *
     * @param weather 天气对象
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        mTitleCityTextView.setText(cityName);
        mTitleUpdateTimeTextView.setText(updateTime);
        mDegreeTextView.setText(degree);
        mWeatherInfoTextView.setText(weatherInfo);

        // 动态加载数据
        mForecastLinearLayout.removeAllViews(); // 把layout容器中的的views视图都移除掉，得到一个空layout容器
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    mForecastLinearLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max + "℃");
            minText.setText(forecast.temperature.min + "℃");
            mForecastLinearLayout.addView(view);
        }

        if (weather.aqi != null) {
            mAqiTextView.setText(weather.aqi.city.aqi);
            mPm25TextView.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        mComfortTextView.setText(comfort);
        mCarWashTextView.setText(carWash);
        mSportTextView.setText(sport);

        mWeatherLayoutScrollView.setVisibility(View.VISIBLE); // 数据加载完毕显示ScrollView

        // 启动后台自动更新服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace(); // 打印错误信息
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this)
                        .edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(mBingPicImageView);
                    }
                });
            }
        });
    }
}
