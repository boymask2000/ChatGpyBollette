package com.boymask.myapplication;

import android.os.Bundle;
import android.util.Base64;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private void callChatt(String pathContent) {

    }

    private void callChatGpt(String pathContent) {
        OpenAIApi api = ApiClient.getClient().create(OpenAIApi.class);
        List<ChatRequest.Message> messages = new ArrayList<>();

        String base64 = getFileContent(pathContent);

        messages.add(new ChatRequest.Message(
                "user",
                "Estrai valori e descrizioni da questa bolletta in base64: " + base64
        ));

        ChatRequest request = new ChatRequest(
                "gpt-4o-mini",
                messages
        );

        Call<ChatResponse> call = api.chat(request);

        ApiResponseHandler.enqueue(call, result -> {

            if (result.isSuccess) {

                String text = result.data
                        .choices.get(0)
                        .message
                        .content;

                Toast.makeText(this, text, Toast.LENGTH_LONG).show();
                textView.setText(text);
            } else {

                Toast.makeText(
                        this,
                        "Errore: " + result.error.message,
                        Toast.LENGTH_LONG
                ).show();
                textView.setText(result.error.message);
            }


        });
    }

    private String getFileContent(String pathContent) {
        File file = new File(pathContent);
        byte[] buffer = new byte[(int) file.length()];

        try {
            try (FileInputStream fis = new FileInputStream(file)) {
                int read = fis.read(buffer);
                if (read != buffer.length) {
                    throw new IOException("Lettura incompleta del file");
                }
            }
        } catch (Exception e) {
        }

        String base64 = Base64.encodeToString(buffer, Base64.DEFAULT);
        return base64;
    }


}