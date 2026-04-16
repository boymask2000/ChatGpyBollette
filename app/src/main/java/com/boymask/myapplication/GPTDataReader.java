package com.boymask.myapplication;

import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.boymask.myapplication.retrofit.ApiClient;
import com.boymask.myapplication.retrofit.ApiResponseHandler;
import com.boymask.myapplication.retrofit.ChatRequest;
import com.boymask.myapplication.retrofit.ChatResponse;
import com.boymask.myapplication.retrofit.OpenAIApi;
import com.boymask.myapplication.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GPTDataReader extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gptdata_reader);

        String pathContent = getIntent().getStringExtra("content");

        textView = findViewById(R.id.textView);

        //     String testo = getIntent().getStringExtra("testo");

        callChatGpt(pathContent);

    }

    private void callChatGpt(String pathContent) {
        File file = new File(pathContent);

        RequestBody requestFile =
                RequestBody.create(file, MediaType.parse("application/octet-stream"));

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        RequestBody purpose =
                RequestBody.create("assistants", MediaType.parse("text/plain"));

        OpenAIApi api = RetrofitClient.getClient();

        // STEP 1: UPLOAD
        api.uploadFile("Bearer " + OpenAIApi.API_KEY, body, purpose)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (!response.isSuccessful()) {
                                System.out.println("Upload error "+response);
                                System.out.println(   response.errorBody().string());
                                return;
                            }

                            String res = response.body().string();
                            JSONObject json = new JSONObject(res);

                            String fileId = json.getString("id");

                            System.out.println("File caricato: " + fileId);

                            // STEP 2: ANALISI
                            analyze(api, OpenAIApi.API_KEY, fileId);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private static void analyze(OpenAIApi api, String apiKey, String fileId) {

        try {
            JSONObject requestJson = new JSONObject();

            requestJson.put("model", "gpt-5.4-mini");

            JSONArray inputArray = new JSONArray();

            JSONObject message = new JSONObject();
            message.put("role", "user");

            JSONArray contentArray = new JSONArray();

// testo richiesta
            contentArray.put(new JSONObject()
                    .put("type", "input_text")
               //     .put("text", "Estrai nome, data, importo e codice fiscale in formato JSON"));
            .put("text", Prompt.PROMPT));

// file tramite file_id
            contentArray.put(new JSONObject()
                    .put("type", "input_file")
                    .put("file_id", fileId));

            message.put("content", contentArray);
            inputArray.put(message);

            requestJson.put("input", inputArray);
            RequestBody body = RequestBody.create(
                    requestJson.toString(),
                    MediaType.parse("application/json")
            );
            api.analyzeFile("Bearer " + apiKey, body)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                if (response.isSuccessful()) {
                                    System.out.println("RISULTATO:");
                                    System.out.println(response.body().string());
                                    reportOutput(response.body().string());
                                } else {
                                    System.out.println("Errore analisi");
                                    System.out.println(   response.errorBody().string());
                                    reportOutput(response.errorBody().string());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void reportOutput(String string) throws JSONException {
        StringBuilder finalText = new StringBuilder();
        JSONObject json = new JSONObject(string);
        JSONArray output = json.getJSONArray("output");

        for (int i = 0; i < output.length(); i++) {
            JSONArray content = output.getJSONObject(i).getJSONArray("content");

            for (int j = 0; j < content.length(); j++) {
                JSONObject item = content.getJSONObject(j);

                if ("output_text".equals(item.getString("type"))) {
                    finalText.append(item.getString("text"));
                }
            }
        }

        System.out.println(finalText.toString());
    }
}