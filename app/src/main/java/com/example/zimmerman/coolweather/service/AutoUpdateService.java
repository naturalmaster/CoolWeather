package com.example.zimmerman.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.zimmerman.coolweather.receiver.AutoUpdateReceiver;
import com.example.zimmerman.coolweather.util.HandleJvheHttpUtil;
import com.example.zimmerman.coolweather.util.HttpCallBackListener;
import com.example.zimmerman.coolweather.util.HttpUtil;
import com.example.zimmerman.coolweather.util.LogUtil;
import com.example.zimmerman.coolweather.util.Utilty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zimmerman on 2016/7/25.
 */
public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("t","service on create!!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("t", "service on destroy!!");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d("update","onStartCommand ,autpUpdate");
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("LongRunningService", "executed at " + new Date().toString());
                updateWeather();
            }
        }).start();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour= 2*60*60*1000;  //设置为每两个小时启动一次更新
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;

        Intent i = new Intent(this,AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String  cityName= sharedPreferences.getString("city_name", "");

        LogUtil.d("t",cityName);
        Map map = new HashMap();
        map.put("cityname",cityName);
//        paramas.put("cityname",countyName);
        map.put("key",HandleJvheHttpUtil.APP_KEY);
        HandleJvheHttpUtil.sendHttpToJvhe(map, new HttpCallBackListener() {
            @Override
            public void onFinish(String request) {
                Utilty.handlJvheWeather(AutoUpdateService.this,request);
            }

            @Override
            public void onError(Exception e) {

                e.printStackTrace();
            }
        });
/*
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String request) {
//                Utilty.handleWeatherResponse(AutoUpdateService.this,request);
            }

            @Override
            public void onError(Exception e) {

                e.printStackTrace();
            }
        });
        */
    }
}
