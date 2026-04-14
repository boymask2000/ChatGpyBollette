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
    TextView textView ;
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
        OpenAIApi api = ApiClient.getClient().create(OpenAIApi.class);
        List<ChatRequest.Message> messages = new ArrayList<>();

        String base64=getFileContent(pathContent);

       /* messages.add(new ChatRequest.Message(
                "user",
                "Spiegami cosa fa Retrofit in Android"
        ));*/
        messages.add(new ChatRequest.Message(
                "user",
                "Estrai valori e descrizioni da questa bolletta in base64: " + base64
        ));

        ChatRequest request = new ChatRequest(
                "gpt-4o-mini",
                messages
        );

        api.chat(request).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                textView.setText(response.toString());
                if (response.isSuccessful() && response.body() != null) {
                    String text = response.body()
                            .choices.get(0)
                            .message
                            .content;
                    Toast.makeText(GPTDataReader.this, text, Toast.LENGTH_LONG).show();
System.out.println(text);
                    textView.setText(text);
                } else {

                    try {
                        String errorJson = response.errorBody().string();

                        textView.setText("ERRORE RAW: " + errorJson);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Toast.makeText(GPTDataReader.this, t.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println(t.getMessage());
                textView.setText(t.getMessage());
            }
        });
    }

    private String getFileContent(String pathContent)  {
        File file = new File(pathContent);
        byte[] buffer = new byte[(int) file.length()];

        try {
            try (FileInputStream fis = new FileInputStream(file)) {
                int read = fis.read(buffer);
                if (read != buffer.length) {
                    throw new IOException("Lettura incompleta del file");
                }
            }
        }catch(Exception e ){}

        String base64 = Base64.encodeToString(buffer, Base64.DEFAULT);
        return base64;
    }

    ;


/*        String apiKey = "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";

        String json = "{"
                + "\"model\":\"gpt-4\","
                //     + "\"messages\":[{\"role\":\"user\",\"content\":\"Analizza questo contenuto:\\n"
                + "\"messages\":[{\"role\":\"user\",\"content\":\""+prompt+":\\n"
                + escapeJSON(contenuto) + "\"}]"
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        byte[] output = response.body().getBytes() ;
        // Salva output
        Files.write(Paths.get("C:\\Users\\Admin\\Desktop\\output.txt"),output);
    }*/
}