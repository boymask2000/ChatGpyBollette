package com.boymask.myapplication;


import android.content.Context;

import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private Properties properties;

    public ConfigManager(Context context) {
        properties = new Properties();
        try {
            InputStream inputStream = context.getAssets().open("application.properties");
            properties.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}