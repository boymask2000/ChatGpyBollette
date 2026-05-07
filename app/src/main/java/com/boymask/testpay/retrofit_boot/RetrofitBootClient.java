package com.boymask.testpay.retrofit_boot;


import com.boymask.RysLogger;
import com.boymask.myapplication.ConfigManager;
import com.boymask.myapplication.MainActivity2;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
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

            /*retrofit = new Retrofit.Builder()
                 //   .baseUrl("http://192.168.1.107:8080/")
                    .baseUrl(urlServer)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();*/


            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)   // importante per risposte lente
                    .writeTimeout(120, TimeUnit.SECONDS)  // importante per upload file
                    .retryOnConnectionFailure(true)
                    .build();

             retrofit = new Retrofit.Builder()
                    .baseUrl(urlServer)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}