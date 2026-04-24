package com.boymask.testpay.retrofit_boot;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBootClient {

    private static Retrofit retrofit;

    public static Retrofit getClient() {

        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.107:8081/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}