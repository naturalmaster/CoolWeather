package com.example.zimmerman.coolweather.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zimmerman.coolweather.R;
import com.example.zimmerman.coolweather.service.AutoUpdateService;
import com.example.zimmerman.coolweather.util.HandleJvheHttpUtil;
import com.example.zimmerman.coolweather.util.HttpCallBackListener;
import com.example.zimmerman.coolweather.util.HttpUtil;
import com.example.zimmerman.coolweather.util.LogUtil;
import com.example.zimmerman.coolweather.util.Utilty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zimmerman on 2016/7/24.
 */
public class WeatherActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout weatherInfoLayout;

    /*城市名*/
    private TextView cityNameText;

    private TextView moon_text;
    /*发布时间*/
    private TextView publishText;

    /*天气信息*/
    private TextView weatherInfoText;
    /*气温1、2*/
    private TextView temp1Text;
    private TextView temp2Text;
    /*x显示当前日期*/
    private TextView currentDateText;
    /*更新和切换城市的按钮*/
    private Button switchCity;
    private Button refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        initView();

//        String countyCode = getIntent().getStringExtra("county_code");
        String countyName = getIntent().getStringExtra("city_name");

//        如果将一下两个注册监听事件的语句放入 iniView() 方法中，那么运行时就会报错 ：IOException
        /*jv
        * 具体情况也不是很清楚，调试得出是this的问题，具体是什么问题就不知道了*/
        switchCity.setOnClickListener(this);
        refresh.setOnClickListener(this);
        if (!TextUtils.isEmpty(countyName)) {
            publishText.setText("同步中");
            cityNameText.setVisibility(View.INVISIBLE);
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            queryFromServer(countyName);
        } else {
            /*
            * 直接显示本地缓存中的天气信息
            * */
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showWeather();
                }
            });
        }

    }

    /*
    初始化各控件
     */
    private void initView() {
        moon_text = (TextView) findViewById(R.id.moon_text);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        weatherInfoText = (TextView) findViewById(R.id.weather_desp);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        temp1Text = (TextView) findViewById(R.id.temp1);
//        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);


        switchCity = (Button) findViewById(R.id.switch_city);
        refresh = (Button) findViewById(R.id.refresh_weather);



    }


    /*
    根据 县级代号查询天气代号
     */
    /*
    private void queryWeatherCode(String countyCode) {

        String address = "http://www.weather.com.cn/data/list3/city"+
                countyCode+".xml";
        queryFromServer(address, "county");
    }
*/

    /*
    根据天气代号查询天气
     */
/*
    private void queryWeatherInfo(String weatherCode) {

        String address = "http://www.weather.com.cn/data/cityinfo/"+
                weatherCode +".html";
        queryFromServer(address, "weathercode");
    }
*/


    /*
    根据传入地址，以及类型 去服务器查询天气信息
    FOR jvhe
     */
    private void queryFromServer( final String countyName) {   //更新天气

        LogUtil.d("jvhe_test","---------queryFromServer---------");
        LogUtil.d("jvhe_test", countyName);

        Map paramas = new HashMap();
        paramas.put("cityname",countyName);
        paramas.put("key",HandleJvheHttpUtil.APP_KEY);
        LogUtil.d("query",countyName);
        HandleJvheHttpUtil.sendHttpToJvhe(paramas, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {


                Utilty.handlJvheWeather(WeatherActivity.this, response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });


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
//        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
//            @Override
//            public void onFinish(String response) {
/*
                if ("county".equals(type)) {
                    if (!TextUtils.isEmpty(request)) {
                        String[] array = request.split("\\|");
                        if (array != null && array.length == 2) {
                            queryWeatherInfo(array[1]);
                        }
                    }
                } else if ("weathercode".equals(type)) {

                    /*
                    这里需要处理返回的天气信息
                     */
/*
                    Utilty.handleWeatherResponse(WeatherActivity.this, request);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
*/
            }
/*
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

    }*/

    /*
    从本地的SharedPreference读取天气信息，并显示在控件上
     */
    private void showWeather() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        LogUtil.d("debug", sharedPreferences.getString("city_name", ""));
        LogUtil.d("debug", String.valueOf(sharedPreferences.getInt("temp", -1)));


        cityNameText.setText(sharedPreferences.getString("city_name", ""));

        moon_text.setText("农历"+sharedPreferences.getString("moon",""));
        temp1Text.setText(String.valueOf(sharedPreferences.getInt("temp", -1)) + "℃");

        weatherInfoText.setText(sharedPreferences.getString("weather_info", ""));
        publishText.setText("今天"+sharedPreferences.getString("publish_time","")+"发布");
        currentDateText.setText(sharedPreferences.getString("date", ""));

        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.switch_city:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中");
                SharedPreferences sharedPreferences = PreferenceManager.
                        getDefaultSharedPreferences(this);
                String countyName = sharedPreferences.getString("city_name","");
                if(!TextUtils.isEmpty(countyName)) {
                    queryFromServer(countyName);
                }
                break;

            default:
                break;
        }
    }
}
