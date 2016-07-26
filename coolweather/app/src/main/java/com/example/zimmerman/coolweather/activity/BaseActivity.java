package com.example.zimmerman.coolweather.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.zimmerman.coolweather.util.LogUtil;

/**
 * Created by Zimmerman on 2016/7/24.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d("test","onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("test", "onDestroy");
    }


}
