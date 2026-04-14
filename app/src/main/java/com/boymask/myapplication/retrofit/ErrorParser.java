package com.boymask.myapplication.retrofit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.ResponseBody;

public class ErrorParser {

    public static ApiError parse(ResponseBody errorBody) {
        try {
            if (errorBody == null) {
                return new ApiError("Unknown error", null, null);
            }

            String json = errorBody.string();

            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            JsonObject err = obj.getAsJsonObject("error");

            return new ApiError(
                    err.get("message").getAsString(),
                    err.has("type") ? err.get("type").getAsString() : null,
                    err.has("code") ? err.get("code").getAsString() : null
            );

        } catch (Exception e) {
            return new ApiError(e.getMessage(), "parse_error", null);
        }
    }
}