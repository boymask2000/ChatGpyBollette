package com.boymask.testpay.retrofit_boot;



import com.boymask.User;
import com.boymask.testpay.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PaymentApi {

    @POST("/api/payment/create-payment-intent")
    Call<PaymentResponse> createPaymentIntent(@Body PaymentRequest request);

    @GET("products")
    Call<List<Product>> getProducts();

    @GET("users/get/{id}")
    Call<User> getUser(@Path("id")  String userid);

    @PUT("/users/put/{id}")
    Call<User> updateUser(
            @Path("id") String userId,
            @Body User request
    );

    @PUT("/users/abbonamento/put/{id}/{abb}")
    Call<User> addAbbonamento(
            @Path("id") String userId,
            @Path("abb") String abb,
            @Body User request
    );
}
