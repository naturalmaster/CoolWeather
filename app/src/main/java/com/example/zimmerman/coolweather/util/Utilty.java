package com.example.zimmerman.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zimmerman.coolweather.R;
import com.example.zimmerman.coolweather.db.CoolWeatherDB;
import com.example.zimmerman.coolweather.model.City;
import com.example.zimmerman.coolweather.model.County;
import com.example.zimmerman.coolweather.model.Province;
import com.example.zimmerman.coolweather.model.RealWeather;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Zimmerman on 2016/7/23.
 */
public class Utilty {

    private static final int ERROR_NAME=207301;
    private static final int ERROR_NAME_NO_FIND=207302;
    private static final int ERROR_INTERNET=207303;


    /*
    解析Province并且将解析后的数据写入数据库
     */
    public synchronized static boolean handleProvinceResponse (CoolWeatherDB db,
                                                               String response) {
        if(!TextUtils.isEmpty(response)){
            String [] allProvinces = response.split(",");
            if (allProvinces !=null && allProvinces.length>0) {
                for (String p:allProvinces) {
                    String [] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    db.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

      /*
    解析city并且将解析后的数据写入数据库
     */
    public synchronized static boolean handleCityResponse (CoolWeatherDB db,
                                                           String response,int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String [] allCities = response.split(",");
            if (allCities != null && allCities.length>0) {
                for (String c: allCities) {
                    String [] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);

                    db.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
        /*
    解析county并且将解析后的数据写入数据库
     */

    public synchronized static boolean handleCountyResponse (CoolWeatherDB db,
                                                             String response,int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String [] allCounties = response.split(",");
            if (allCounties != null && allCounties.length>0) {
                for (String c: allCounties) {
                    String [] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);

                    db.saveCounty(county);
                }
                return true;
            }
        }
        return  false;
    }


    public static void handlJvheWeather (Context context,String response) {

        int errorCode = 0;

        JSONObject object = null;
        try {
            object = new JSONObject(response);
            errorCode = object.getInt("error_code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //查找成功
        LogUtil.e("error_code",response);
            if (errorCode == 0) {
                try {
                    JSONObject weatherInfojs = object.getJSONObject("result");
                    JSONObject realTimeWeatherInfo =weatherInfojs.getJSONObject("data").
                            getJSONObject("realtime");
                    JSONObject weatherNow = realTimeWeatherInfo.getJSONObject("weather");

                    RealWeather weather = new RealWeather();

                    weather.setTemp(weatherNow.getInt("temperature"));
                    weather.setPublishTime(realTimeWeatherInfo.getString("time"));
                    weather.setWeatherInfo(weatherNow.getString("info"));
                    weather.setDate(realTimeWeatherInfo.getString("date"));
                    weather.setMoon(realTimeWeatherInfo.getString("moon"));
                    weather.setCityName(realTimeWeatherInfo.getString("city_name"));

                    saveWeatherInfoForJvhe(context, weather);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
//                TextView publish_text = (TextView) findViewById(R.id.publish_text);

                switch (errorCode) {
                    
                    case ERROR_NAME:
                        LogUtil.e("error_code", "错误的查询城市名");
//                        Toast.makeText(context, "错误的查询城市名"
//                                , Toast.LENGTH_LONG);
                        break;
                    case ERROR_NAME_NO_FIND:
                        LogUtil.e("error_code","查询不到该城市的相关信息");
//                        Toast.makeText(context, "查询不到该城市的相关信息"
//                                , Toast.LENGTH_LONG);
                        break;
                    case ERROR_INTERNET:
                        LogUtil.e("error_code","网络错误，请重试");
//                        Toast.makeText(context,"网络错误，请重试"
//                                ,Toast.LENGTH_LONG);
                        break;
                    default:
                        break;
                }

            }

    }
    /*
    解析服务器返回的JSON数据，并将解析出的数据存储到本地

    @FOR COOL
     */
/*
    public static void handleWeatherResponse(Context context ,String response) {
        LogUtil.d("test","address: ");
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo (context,cityName,weatherCode,temp1,temp2,
                    weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    */
//FOR JVHE
    private static void saveWeatherInfoForJvhe (Context context,RealWeather weather) {

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name", weather.getCityName());
        editor.putString("weather_info", weather.getWeatherInfo());
        editor.putInt("temp", weather.getTemp());
        editor.putString("date", weather.getDate());
        editor.putString("moon", weather.getMoon());
        editor.putString("publish_time", weather.getPublishTime());

        editor.commit();
    }


    /*
        @FOR COOL
     */
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,
                                       String temp1,String temp2,
                                       String weatherDesp,String publishTime ) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",dateFormat.format(new Date()));
        editor.commit();
    }

}
