package com.example.zimmerman.coolweather.util;

import android.text.TextUtils;

import com.example.zimmerman.coolweather.db.CoolWeatherDB;
import com.example.zimmerman.coolweather.model.City;
import com.example.zimmerman.coolweather.model.County;
import com.example.zimmerman.coolweather.model.Province;

/**
 * Created by Zimmerman on 2016/7/23.
 */
public class Utilty {
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


}
