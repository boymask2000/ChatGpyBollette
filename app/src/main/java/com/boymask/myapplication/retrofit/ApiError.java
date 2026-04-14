package com.boymask.myapplication.retrofit;

public class ApiError {
    public String message;
    public String type;
    public String code;

    public ApiError(String message, String type, String code) {
        this.message = message;
        this.type = type;
        this.code = code;
    }
}