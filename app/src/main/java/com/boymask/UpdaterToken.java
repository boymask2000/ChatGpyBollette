package com.boymask;

import android.util.Log;

import com.boymask.testpay.retrofit_boot.PaymentApi;
import com.boymask.testpay.retrofit_boot.PaymentResponse;
import com.boymask.testpay.retrofit_boot.RetrofitBootClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdaterToken {
    public static void update(long n, boolean incBollette ){
        PaymentApi api = RetrofitBootClient.getClient().create(PaymentApi.class);
        UpdaterTokenBean bean = new UpdaterTokenBean(n, incBollette);

        Call<UpdaterTokenBean> call = api.updateToken(bean);

        call.enqueue(new Callback<UpdaterTokenBean>() {
            @Override
            public void onResponse(Call<UpdaterTokenBean> call, Response<UpdaterTokenBean> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Success: " + response.body());

                } else {
                    Log.e("API", "Errore: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            String error = response.errorBody().string();
                            Log.e("API", "Errore body: " + error);
                        }
                    } catch (IOException e) {
                        Log.e("API", "Errore lettura errorBody");
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdaterTokenBean> call, Throwable t) {
                Log.e("API", "Errore rete: " + t.getMessage());
            }
        });
    }
}
