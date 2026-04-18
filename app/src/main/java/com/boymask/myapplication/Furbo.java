package com.boymask.myapplication;

import android.content.Context;

public class Furbo {
    public static String ketKey(Context context) {

        ConfigManager config = new ConfigManager(context);
        String scadenza1 = config.get("scadenza16");
        String scadenza2 = config.get("scadenza56");
        String scadenza3 = config.get("scadenza71");
        try {
            return FileUtil.dec(scadenza1,scadenza2,scadenza3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
/*        FirebaseApp.initializeApp(context);
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(context.getMainExecutor(), new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d("TAG", "Config params updated: " + updated);
                            Log.d("TAG", "Fetch and activate succeeded");

                        } else {
                            Log.d("TAG", "Fetch and activate failed");
                        }
                        String welcomeMessage = mFirebaseRemoteConfig.getString("KEY");
                        Log.d("TAG", welcomeMessage);
                    }
                });
        return scadenza1;*/
    }
}

