package com.boymask.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.boymask.myapplication.retrofit.OpenAIApi;
import com.boymask.myapplication.retrofit.RetrofitClient;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GPTDataReaderPDF extends AppCompatActivity {
    String API_KEY = MainActivity2.API_KEY;

        private static final int PICK_PDF = 1;

        private TextView txtResult;
        private String apiKey =API_KEY;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gptdata_reader_pdf);

            Button btn = findViewById(R.id.btnSelect);
            txtResult = findViewById(R.id.txtResult);

            btn.setOnClickListener(v -> openFilePicker());
        }

        private void openFilePicker() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, PICK_PDF);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_PDF && resultCode == RESULT_OK) {
                Uri uri = data.getData();
                processPdf(uri);
            }
        }

        private void processPdf(Uri uri) {
            new Thread(() -> {
                try {
                    File file = FileUtil.from(this, uri);

              //      String text = PdfProcessor.extractAndFilter(file);
                 //   String prompt = PromptBuilder.build(text);

                    PdfTextExtractor.extract(this, uri, t -> {

                        String filtered = BillFilter.clean(t);

                        String prompt = PromptBuilder.build(filtered);

                        callApi(prompt);

                    });

             /*       runOnUiThread(() -> txtResult.setText("Analisi in corso..."));

                    callApi(prompt);*/

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> txtResult.setText("Errore PDF"));
                }
            }).start();
        }

        private void callApi(String prompt) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openai.com/")
                    .client(client)
                    .build();

            OpenAIApi api = retrofit.create(OpenAIApi.class);

            String json = "{ \"model\": \"gpt-5.4\", \"input\": \"" +
                    prompt.replace("\"", "\\\"") + "\" }";

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), json);

            api.analyzeFile("Bearer " + API_KEY, body)
                    .enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                if (response.isSuccessful() && response.body() != null) {

                                    String res = response.body().string();
                                    BollettaData data = Parser.parse(res);

                                    runOnUiThread(() -> showResult(data));

                                } else {
                                    runOnUiThread(() -> {
                                        try {
                                            txtResult.setText("Errore API "+response.errorBody().string());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            runOnUiThread(() -> txtResult.setText("Errore rete"));
                        }
                    });
        }

        private void showResult(BollettaData b) {
            txtResult.setText(
                    "Intestatario: " + b.intestatario + "\n\n" +
                            "Totale: " + b.totale + "\n" +
                            "Consumo: " + b.consumoSmc + "\n" +
                            "Periodo: " + b.periodo + "\n" +
                            "Fornitore: " + b.fornitore
            );
        }
    }