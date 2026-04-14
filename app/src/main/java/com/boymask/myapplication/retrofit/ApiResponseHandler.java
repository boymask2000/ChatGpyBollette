package com.boymask.myapplication.retrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiResponseHandler {

    public static <T> void enqueue(Call<T> call, ApiCallback<T> callback) {

        call.enqueue(new Callback<T>() {

            @Override
            public void onResponse(Call<T> call, Response<T> response) {

                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                } else {
                    ApiError error = ErrorParser.parse(response.errorBody());
                    callback.onResult(Result.error(error));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {

                ApiError error = new ApiError(
                        t.getMessage(),
                        "network_error",
                        null
                );

                callback.onResult(Result.error(error));
            }
        });
    }

    public interface ApiCallback<T> {
        void onResult(Result<T> result);
    }
}