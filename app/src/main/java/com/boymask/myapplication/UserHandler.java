package com.boymask.myapplication;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.boymask.RysLogger;
import com.boymask.User;
import com.boymask.myapplication.database.Status;
import com.boymask.testpay.retrofit_boot.PaymentApi;
import com.boymask.testpay.retrofit_boot.PaymentResponse;
import com.boymask.testpay.retrofit_boot.RetrofitBootClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHandler {
    private static final PaymentApi api;
    private static User user;
    private static ImageButton button;
    private static TextView messaggio;
    private static Activity context;

    static {
        api = RetrofitBootClient.getClient().create(PaymentApi.class);
    }

    public static void checkBolletteDisponibili(ImageButton button, String userid, TextView messaggio, Activity context) {
        UserHandler.button = button;
        UserHandler.messaggio = messaggio;
        UserHandler.context = context;
        Call<User> call = api.getUser(userid);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    Log.d("API", "Nome: " + user);
                    RysLogger.add("User: "+user);
                    if (user.getBolletteTotali() - user.getBolletteAnalizzate() <= 0) {
                        context.runOnUiThread(() -> {
                            button.setEnabled(false);
                            messaggio.setText("Hai esaurito il numero di bollette disponibili");
                        });
                    }

                } else {
                    Log.e("API", "Errore risposta: " + response.code());
                    RysLogger.add("Errore risposta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API", "Errore rete: " + t.getMessage());
                RysLogger.add("Errore rete: " + t.getMessage());
            }
        });
    }

    public static User getUser() throws IOException {
        Call<User> call = api.getUser(user.getUserid());
        Response<User> response = call.execute();
        user = response.body();
        return user;
    }

    public static String getUsername(Context context) {
        return IDManager.getDeviceId(context);

    }

    public static void decBollette() {


        user.setBolletteAnalizzate(user.getBolletteAnalizzate() + 1);
        updateUser();
    }

    private static void updateUser() {

        Call<User> call = api.updateUser(user.getUserid(), user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    if (user.getBolletteTotali() - user.getBolletteAnalizzate() <= 0) {
                        context.runOnUiThread(() -> {
                            button.setEnabled(false);
                            messaggio.setText("Hai esaurito il numero di bollette disponibili");
                        });
                    }
                    Log.d("API", "Aggiornato: " + user);
                } else {
                    Log.e("API", "Errore PUT: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API", "Errore rete: " + t.getMessage());
                RysLogger.add("Errore rete: " + t.getMessage());
            }
        });
    }
}
