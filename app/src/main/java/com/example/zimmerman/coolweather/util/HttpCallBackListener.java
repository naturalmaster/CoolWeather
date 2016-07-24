package com.example.zimmerman.coolweather.util;

/**
 * Created by Zimmerman on 2016/7/23.
 */
public interface HttpCallBackListener {
    void onFinish(String request);

    void onError(Exception e);

}
