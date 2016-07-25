package com.example.zimmerman.coolweather.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zimmerman.coolweather.R;
import com.example.zimmerman.coolweather.util.HttpCallBackListener;
import com.example.zimmerman.coolweather.util.HttpUtil;
import com.example.zimmerman.coolweather.util.Utilty;

/**
 * Created by Zimmerman on 2016/7/24.
 */
public class WeatherActivity extends BaseActivity {

    private LinearLayout weatherInfoLayout;

    /*城市名*/
    private TextView cityNameText;

    /*发布时间*/
    private TextView publishText;

    /*天气信息*/
    private TextView weatherInfoText;
    /*气温1、2*/
    private TextView temp1Text;
    private TextView temp2Text;
    /*x显示当前日期*/
    private TextView currentDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        initView();
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            publishText.setText("同步中");
            cityNameText.setVisibility(View.INVISIBLE);
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            /*
            * 直接显示本地缓存中的天气信息
            * */
            showWeather();
        }

    }

    /*
    初始化各控件
     */
    private void initView() {

        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        weatherInfoText = (TextView) findViewById(R.id.weather_desp);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
    }

    /*
    根据 县级代号查询天气代号
     */
    private void queryWeatherCode(String countyCode) {

        String address = "http://www.weather.com.cn/data/list3/city"+
                countyCode+".xml";
        queryFromServer(address, "county");
    }

    /*
    根据天气代号查询天气
     */
    private void queryWeatherInfo(String weatherCode) {

        String address = "http://www.weather.com.cn/data/cityinfo/"+
                weatherCode +".html";
        queryFromServer(address, "weathercode");
    }

    /*
    根据传入地址，以及类型 去服务器查询天气信息 或天气代号
     */
    private void queryFromServer(String address, final String type) {

        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String request) {
                if (type.equals("county")) {
                    if (!TextUtils.isEmpty(request)) {
                        String [] array = request.split("\\|");
                        if (array!= null&& array.length ==2){
                            queryWeatherInfo(array[1]);
                        }
                    }
                } else  if (type.equals("weathercode")) {

                    /*
                    这里需要处理返回的天气信息
                     */

                    Utilty.handleWeatherResponse(WeatherActivity.this,request);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败...");
                    }
                });
            }
        });

    }

    /*
    从本地的SharedPreference读取天气信息，并显示在控件上
     */
    private void showWeather() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        cityNameText.setText(sharedPreferences.getString("city_name",""));
        temp1Text.setText(sharedPreferences.getString("temp1",""));
        temp2Text.setText(sharedPreferences.getString("temp2",""));
        weatherInfoText.setText(sharedPreferences.getString("weather_desp",""));
        publishText.setText("今天"+sharedPreferences.getString("publish_time"+"发布",""));
        currentDateText.setText(sharedPreferences.getString("current_date",""));

        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }
}
