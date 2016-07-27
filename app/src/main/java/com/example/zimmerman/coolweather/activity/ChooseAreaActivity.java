package com.example.zimmerman.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zimmerman.coolweather.R;
import com.example.zimmerman.coolweather.db.CoolWeatherDB;
import com.example.zimmerman.coolweather.model.City;
import com.example.zimmerman.coolweather.model.County;
import com.example.zimmerman.coolweather.model.Province;
import com.example.zimmerman.coolweather.util.HttpCallBackListener;
import com.example.zimmerman.coolweather.util.HttpUtil;
import com.example.zimmerman.coolweather.util.LogUtil;
import com.example.zimmerman.coolweather.util.Utilty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zimmerman on 2016/7/24.
 */
public class ChooseAreaActivity extends BaseActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView tilteText;
    private ListView listView;
    private ArrayAdapter <String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<>();
    /*
    省列表
     */
    private List <Province> provinceList;
    /*
    城市列表
    * */
    private List <City> cityList;
    /*
    县城列表
     */
    private List<County> countyList;
    /*
    选中的省份
     */
    private Province selectedProvince;
      /*
    选中的城市
     */
    private City selectedCity;
      /*
    选中的县城
     */
    private County selectedCounty;
    /*
    当前选中的级别
     */
    private int currentLevel;

    /*是否从天气面板跳过来*/
    private boolean isFromWeatherLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherLayout = getIntent().getBooleanExtra("from_weather_activity",
                false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        /*
        如果是从WeatherActivity跳过来，那么就要进入选择界面
         */
        if (sharedPreferences.getBoolean("city_selected",false)
                && !isFromWeatherLayout) {

            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        tilteText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
//                    String countyCode = countyList.get(position).getCountyCode();
                    //modify for new jvhe
                    String countyName = countyList.get(position).getCountyName();
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("city_name",countyName);
                    startActivity(intent);
                    finish();
                }
            }
        });
          queryProvinces();

    }

    /*f
    查找省、市、县的数据。优先从数据库找。找不到就去服务器查找
     */

    private void queryProvinces() {

        provinceList = coolWeatherDB.loadProvince();
        if (provinceList.size() >0) {
            dataList.clear();
            for (Province p :
                    provinceList) {
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            tilteText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null,"province");
        }
    }

    private void queryCities () {
        cityList = coolWeatherDB.loadCity(selectedProvince.getId());
        if (cityList.size() >0) {
            dataList.clear();
            for (City city :
                    cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            tilteText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    private void queryCounties () {

        countyList = coolWeatherDB.loadCounty(selectedCity.getId());
        if (countyList.size() >0) {
            dataList.clear();
            for (County county :
                    countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            tilteText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    /*
    从服务器上查找
     */

    private void queryFromServer (final String code, final String type) { //code 城市代号，查找市、县时使用

        String address;

        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            boolean result=false;
            @Override
            public void onFinish(String request) {

                if ("province".equals(type)) {
                    result = Utilty.handleProvinceResponse(coolWeatherDB,request);
                } else if ("city".equals(type)) {
                    result = Utilty.handleCityResponse(coolWeatherDB,request,
                            selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utilty.handleCountyResponse(coolWeatherDB, request,
                            selectedCity.getId());
                }

                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }



            @Override
            public void onError(Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });
    }

    /*
    进度对话框
     */

    private void showProgressDialog () {

        if (progressDialog ==null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("loading...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /*
    关闭进度对话框
     */
    private void closeProgressDialog() {

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /*
    修改BACK建的作用
     */

    @Override
    public void onBackPressed (){
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            if (isFromWeatherLayout) {
                Intent intent = new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("test","chooseAreaActivity++destroy");
    }
}
