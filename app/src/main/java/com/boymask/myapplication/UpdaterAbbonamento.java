package com.boymask.myapplication;

import android.util.Log;

import com.boymask.RysLogger;
import com.boymask.User;
import com.boymask.testpay.Product;
import com.boymask.testpay.retrofit_boot.PaymentApi;
import com.boymask.testpay.retrofit_boot.RetrofitBootClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdaterAbbonamento {
    private static final PaymentApi api ;

    static {
        api = RetrofitBootClient.getClient().create(PaymentApi.class);
    }

    public static void add(Product selectedProduct, MainPayActivity mainPayActivity) {
        String user = UserHandler.getUsername(mainPayActivity);


        Call<User> call = api.addAbbonamento(user, selectedProduct.name, new User());

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                  /*  User user = response.body();
                    if (user.getBolletteTotali() - user.getBolletteAnalizzate() <= 0) {
                        context.runOnUiThread(() -> {
                            button.setEnabled(false);
                            messaggio.setText("Hai esaurito il numero di bollette disponibili");
                        });
                    }*/
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
