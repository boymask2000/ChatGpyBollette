package com.boymask.myapplication.retrofit;

import java.util.Map;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiGpt {

    @POST("/api/gpt/analyze")
    Call<ResponseBody> analyze(@Body Map<String, Object> body);

    @POST("/api/gpt/analyzepdf")
    Call<ResponseBody> analyzepdf(@Body Map<String, Object> body);
}