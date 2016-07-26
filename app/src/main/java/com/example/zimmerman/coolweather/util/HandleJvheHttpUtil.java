package com.example.zimmerman.coolweather.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Zimmerman on 2016/7/26.
 */
public class HandleJvheHttpUtil {

    public static final String APP_KEY= "2ebaf2f6d20a9e0175028046d41e88eb";

    public static final String addressOrigin =
            "http://op.juhe.cn/onebox/weather/query";
    public static void sendHttpToJvhe(final Map params,    //组装起来的数据
                               final HttpCallBackListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String address = addressOrigin;
                HttpURLConnection connection =null;
                StringBuilder response = new StringBuilder();
                BufferedReader reader = null;

                address = address + "?" +urlencode(params);

                LogUtil.i("query_cast",address);
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    connection.connect();

                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));

                    String line;
                    while ( ( line = reader.readLine() ) != null) {
                        response.append(line);
                    }


                    if (listener != null) {
                        listener.onFinish(response.toString());
                    }

                } catch (MalformedURLException e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                    e.printStackTrace();
                } catch (IOException e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

            }
        }).start();

    }

    private static String  urlencode(Map<String,String> data) {
        StringBuilder address = new StringBuilder();
        for (Map.Entry map :
                data.entrySet()) {
            try {
                address.append(map.getKey()).append("=")
                        .append(URLEncoder.encode(map.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
//op.juhe.cn/onebox/weather/query?cityname=%E6%B2%B3%E6%BA%90&dtype=json&key=2ebaf2f6d20a9e0175028046d41e88eb
        return address.toString();
    }
}
