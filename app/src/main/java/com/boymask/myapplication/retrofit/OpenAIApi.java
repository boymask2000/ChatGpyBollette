package com.boymask.myapplication.retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface OpenAIApi {

    @Multipart
    @POST("v1/files")
    Call<ResponseBody> uploadFile(
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part file,
            @Part("purpose") RequestBody purpose
    );

    @POST("v1/responses")
    Call<ResponseBody> analyzeFile(
            @Header("Authorization") String authorization,
            @Body RequestBody body
    );
}