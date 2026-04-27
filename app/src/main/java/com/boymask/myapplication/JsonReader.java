package com.boymask.myapplication;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {
    public static String getTokens(JSONObject jsonObject) throws JSONException {
        String text = jsonObject
                .getJSONObject("usage")
                //    .getJSONObject(0)

                .getString("total_tokens");
        text = text.replace("```json", "")
                .replace("```", "")
                .trim();
        return text;
    }
}
