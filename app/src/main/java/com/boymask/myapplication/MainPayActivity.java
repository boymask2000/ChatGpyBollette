package com.boymask.myapplication;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.boymask.RysLogger;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPayActivity extends AppCompatActivity {

    private ActivityMainPayBinding binding;

    private PaymentSheet paymentSheet;
    private String clientSecret;

    private Product selectedProduct;
    private PaymentApi api;

    // Lista condivisa
    private final List<Product> products = new ArrayList<>();

    // Adapter globale
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainPayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        api = RetrofitBootClient.getClient().create(PaymentApi.class);

        Button esci = findViewById(R.id.esci);

        esci.setOnClickListener(v -> finish());

        // Stripe publishable key
      //  51TPKCpGsszBGGRxAn8U8aok7IWgmqn9bhOyTf0mc1iuJQh7kNO2U3Dp5CyOkajZBEwSwoGjH5PXlcotHrbJ5BZiC00m4Ui546G
     //           pk_test_51TPKCpGsszBGGRxAkgkQHGudbvT09mVP2X85zgflyC4zWxff5PdLQoVMTq1yXQpxJEQtSUS7lWqYhuDVQH25bQug00ln388y8u
    //    PaymentConfiguration.init(getApplicationContext(), "pk_test_51TPKCpGsszBGGRxAkgkQHGudbvT09mVP2X85zgflyC4zWxff5PdLQoVMTq1yXQpxJEQtSUS7lWqYhuDVQH25bQug00ln388y8u");
        PaymentConfiguration.init(getApplicationContext(), MainActivity2.stripeKey);
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        setupRecyclerView();

        loadProducts();
    }

    private void setupRecyclerView() {

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductAdapter(products, product -> {

            selectedProduct = product;

            Log.d("PRODUCT_SELECTED", product.name);

            startPayment();
        });

        binding.recyclerView.setAdapter(adapter);
    }

    private void loadProducts() {

        showLoading(true);

        api.getProducts().enqueue(new Callback<List<Product>>() {

            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {

                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {

                    products.clear();

                    products.addAll(response.body());

                    adapter.notifyDataSetChanged();

                    for (Product p : products) {
                        Log.d("PRODUCT", p.name + " - " + p.price);
                    }

                } else {

                    show("Errore caricamento prodotti");

                    if (response.errorBody() != null) {

                        try {

                            String error = response.errorBody().string();

                            Log.e("API_ERROR", error);

                            RysLogger.add(error);

                        } catch (IOException e) {

                            Log.e("ERROR", e.getMessage(), e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

                showLoading(false);

                Log.e("ERROR", t.getMessage(), t);

                RysLogger.add(t);

                show("Errore rete");
            }
        });
    }

    private void startPayment() {

        if (selectedProduct == null) {
            return;
        }

        Log.d("PRODUCT", selectedProduct.name);
        Log.d("PRODUCT", "" + selectedProduct.price);
        Log.d("PRODUCT", selectedProduct.id);

        // Stripe usa i centesimi
      //  long amount = Math.round(selectedProduct.price * 100);
        long amount =selectedProduct.price;
        PaymentRequest request = new PaymentRequest(selectedProduct.name, amount);

        showLoading(true);

        api.createPaymentIntent(request).enqueue(new Callback<PaymentResponse>() {

            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {

                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {

                    clientSecret = response.body().getClientSecret();

                    Log.d("STRIPE", "ClientSecret: " + clientSecret);

                    openPaymentSheet();

                } else {

                    show("Errore server Stripe");

                    if (response.errorBody() != null) {

                        try {

                            String error = response.errorBody().string();

                            Log.e("STRIPE_ERROR", error);

                        } catch (IOException e) {

                            Log.e("ERROR", e.getMessage(), e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {

                showLoading(false);

                Log.e("STRIPE", t.getMessage(), t);

                show("Errore rete");
            }
        });
    }

    private void openPaymentSheet() {

        paymentSheet.presentWithPaymentIntent(clientSecret, new PaymentSheet.Configuration("My Shop"));
    }

    private void onPaymentResult(@NonNull PaymentSheetResult result) {

        if (result instanceof PaymentSheetResult.Completed) {

            show("Pagamento completato 🎉");

            UpdaterAbbonamento.add(selectedProduct, this);

        } else if (result instanceof PaymentSheetResult.Canceled) {

            show("Pagamento annullato");

        } else if (result instanceof PaymentSheetResult.Failed) {

            PaymentSheetResult.Failed failed = (PaymentSheetResult.Failed) result;

            Log.e("STRIPE", failed.getError().getMessage(), failed.getError());

            show("Errore: " + failed.getError().getMessage());
        }
    }

    private void showLoading(boolean show) {

        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void show(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}