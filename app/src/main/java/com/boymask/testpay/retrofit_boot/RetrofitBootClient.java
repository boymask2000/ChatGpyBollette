package com.boymask.testpay.retrofit_boot;


import com.boymask.RysLogger;
import com.boymask.myapplication.ConfigManager;
import com.boymask.myapplication.MainActivity2;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBootClient {
    private static String urlServer = null;
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (urlServer == null) {
            ConfigManager config = new ConfigManager(MainActivity2.context);
            urlServer = config.get("url_server");
            RysLogger.add("server addres: "+urlServer);
        }
        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                 //   .baseUrl("http://192.168.1.107:8080/")
                    .baseUrl(urlServer)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}