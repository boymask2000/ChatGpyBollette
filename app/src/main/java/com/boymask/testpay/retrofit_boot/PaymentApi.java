package com.boymask.testpay.retrofit_boot;



import com.boymask.UpdaterTokenBean;
import com.boymask.testpay.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface PaymentApi {

    @POST("/api/payment/create-payment-intent")
    Call<PaymentResponse> createPaymentIntent(@Body PaymentRequest request);

    @GET("products")
    Call<List<Product>> getProducts();

    @PUT("update_token")
    Call<UpdaterTokenBean> updateToken( @Body UpdaterTokenBean n);
}
