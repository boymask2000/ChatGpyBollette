package com.boymask.myapplication;


import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.boymask.myapplication.databinding.ActivityMainPayBinding;
import com.boymask.testpay.Product;
import com.boymask.testpay.ProductAdapter;

import com.boymask.testpay.retrofit_boot.PaymentApi;
import com.boymask.testpay.retrofit_boot.PaymentRequest;
import com.boymask.testpay.retrofit_boot.PaymentResponse;
import com.boymask.testpay.retrofit_boot.RetrofitBootClient;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.*;

public class MainPayActivity extends AppCompatActivity {

    private ActivityMainPayBinding binding;

    private PaymentSheet paymentSheet;
    private String clientSecret;

    private Product selectedProduct;
    private PaymentApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        api = RetrofitBootClient.getClient().create(PaymentApi.class);

        binding = ActivityMainPayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button esci = findViewById(R.id.esci);

        esci.setOnClickListener(v -> {

            MainPayActivity.this.finish();
        });

        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51TPKCpGsszBGGRxAkgkQHGudbvT09mVP2X85zgflyC4zWxff5PdLQoVMTq1yXQpxJEQtSUS7lWqYhuDVQH25bQug00ln388y8u"
        );

        paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        setupProducts();
    }

    private void setupProducts() {
        loadProducts(api);

    }

    private void startPayment() {

        if (selectedProduct == null) return;

        showLoading(true);

        PaymentRequest request = new PaymentRequest(
                selectedProduct.name,
                (int) selectedProduct.price
        );

        api.createPaymentIntent(request).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {

                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    clientSecret = response.body().getClientSecret();
                    openPaymentSheet();
                } else {
                    show("Errore server");
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                showLoading(false);
                show("Errore rete");
            }
        });
    }

    private void loadProducts(PaymentApi apiService) {
        List<Product> products = new ArrayList<>();

        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    for (Product p : response.body()) {
                        Log.d("PRODUCT", p.name + " - " + p.price);
                        products.add(p);
                    }
                    ProductAdapter adapter = new ProductAdapter(products, product -> {
                        selectedProduct = product;
                        startPayment();
                    });
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(MainPayActivity.this));
                    binding.recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });

    }

    private void openPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
                clientSecret,
                new PaymentSheet.Configuration("My Shop")
        );
    }

    private void onPaymentResult(@NonNull PaymentSheetResult result) {

        if (result instanceof PaymentSheetResult.Completed) {
            show("Pagamento OK 🎉");

        } else if (result instanceof PaymentSheetResult.Canceled) {
            show("Annullato");

        } else {
            show("Errore pagamento");
        }
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void show(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}