package com.example.zimmerman.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.zimmerman.coolweather.model.City;
import com.example.zimmerman.coolweather.model.County;
import com.example.zimmerman.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zimmerman on 2016/7/23.
 */
public class CoolWeatherDB {

    /*
    数据库名
     */
    public static final String DB_NAME = "cool_weather";
    /*
    数据库版本
     */
    public static final int VERSION = 1;

    private static CoolWeatherDB coolWeatherDB;

    private SQLiteDatabase db;

    /*
    构造方法私有
     */
    private CoolWeatherDB (Context context) {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
                DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /*
    获取CoolWeatherDB的实例
     */

    public synchronized static CoolWeatherDB getInstance(Context context) {
        if(coolWeatherDB == null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    /*
    将Province存入数据库
     */

    public void saveProvince(Province province)  {
        if(province != null) {
            ContentValues value = new ContentValues();

            value.put("province_name",province.getProvinceName());
            value.put("province_code",province.getProvinceCode());
            db.insert("Province",null,value);

        }
    }
    /*
    从数据库读取 全国所有省份信息
     */
    public List<Province> loadProvince () {
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("province",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }

        if (cursor !=null) {
            cursor.close();
        }

        return list;
    }

    /*
    将City写入数据库
     */
    public void saveCity(City city) {
        ContentValues values = new ContentValues();
        values.put("city_name",city.getCityName());
        values.put("city_code",city.getCityCode());
        values.put("province_id",city.getProvinceId());
        db.insert("City", null, values);
    }

    /*
    从数据库读取城市信息
     */

    public List<City> loadCity(int provinceId) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("City",null,"province_id = ?",
                new String[]{  String.valueOf(provinceId)},null,null,null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return  list;
    }

    /*
    把county的信息存储到数据库
     */

    public void saveCounty (County county) {
        ContentValues values = new ContentValues();
        values.put("county_name",county.getCountyName());
        values.put("county_code",county.getCountyCode());
        values.put("city_id",county.getCityId());
        db.insert("County",null,values);
    }

    /*
    从数据库读取County的信息

     */
    public List<County> loadCounty(int cityId) {
        List <County> list = new ArrayList<>();
        Cursor cursor = db.query("County",null,"city_id = ?",new String[]{ String.valueOf(cityId)}
                ,null,null,null);
        if (cursor.moveToFirst()) {
            do {

                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                list.add(county);
            } while (cursor.moveToNext());

        }
        if (cursor!= null) {
            cursor.close();
        }

        return list;
    }
}
