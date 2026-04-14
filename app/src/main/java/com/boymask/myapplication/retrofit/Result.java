package com.boymask.myapplication.retrofit;

public class Result<T> {

    public final T data;
    public final ApiError error;
    public final boolean isSuccess;

    private Result(T data, ApiError error, boolean isSuccess) {
        this.data = data;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data, null, true);
    }

    public static <T> Result<T> error(ApiError error) {
        return new Result<>(null, error, false);
    }
}