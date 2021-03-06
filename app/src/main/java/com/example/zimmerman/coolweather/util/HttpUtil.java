package com.example.zimmerman.coolweather.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Zimmerman on 2016/7/23.
 */
public class HttpUtil {

    public static void sendHttpRequest(final String address,
                                       final HttpCallBackListener listener) {

//        BufferedReader reader;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                        URL url= new URL(address);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);

                        InputStream in = connection.getInputStream();
                        reader = new BufferedReader(
                                new InputStreamReader(in)
                        );

                        StringBuilder response = new StringBuilder();
                        String line;

                        while(
                                ( line = reader.readLine() )
                                        != null
                                ) {
                            LogUtil.d("test",line);
                            response.append(line);
                            LogUtil.d("test", " after data");
                        }
                        LogUtil.d("test","after while\n"+"line:"+line +"  response: "+response);

                        if (listener != null) {
                            LogUtil.d("test", "now  I am goint to enter on Finish");
                            LogUtil.d("test", "address: " + address);
                            listener.onFinish(response.toString());
                            LogUtil.d("test", "now  I am goint to OUT FROM Finish");

                        }

                    } catch (MalformedURLException e) {
                        if (listener != null) {
                            LogUtil.e("test", "+++MalformedURLException");
                            listener.onError(e);
                        }
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (listener != null) {
                            LogUtil.e("test","+++IOException" );
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
}
