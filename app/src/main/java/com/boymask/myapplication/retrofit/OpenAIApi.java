package com.boymask.myapplication.retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface OpenAIApi {
    public static String API_KEY="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";

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