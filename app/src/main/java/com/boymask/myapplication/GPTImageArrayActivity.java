package com.boymask.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boymask.myapplication.listaparametri.RowModel;
import com.boymask.myapplication.listaparametri.TableAdapter;
import com.boymask.myapplication.retrofit.OpenAIApi;
import com.boymask.myapplication.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GPTImageArrayActivity extends AppCompatActivity {
    String API_KEY = MainActivity2.API_KEY;
    private ArrayList<RowModel> data = new ArrayList<>();
    private Button askGpt;
    private View loadingContainer;
    private View progressBar;
    private View loadingText;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gptdata_reader);

        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);
        askGpt = findViewById(R.id.askgpt);
        loadingContainer = findViewById(R.id.loadingContainer);
        recyclerView = findViewById(R.id.recyclerView);

// all'inizio mostro loading e nascondo lista
        loadingContainer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        askGpt.setVisibility(View.GONE);

        ArrayList<String> paths = getIntent().getStringArrayListExtra("content");
        for (String uri : paths) {
            // QUI puoi usarle (upload, preview ecc.)
            System.out.println("sURI: " + uri);
        }
        analyzeImages(paths);
    }
    private void analyzeImages(ArrayList<String> imagePaths) {

        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put("model", Prompt.MODEL);

            JSONArray inputArray = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");

            JSONArray contentArray = new JSONArray();

            // 🧠 Prompt testuale
            contentArray.put(new JSONObject()
                    .put("type", "input_text")
                    .put("text", Prompt.PROMPT_ASK));

            // 🖼️ immagini
            for (String path : imagePaths) {
                String base64 = encodeImageToBase64(path);

                contentArray.put(new JSONObject()
                        .put("type", "input_image")
                        .put("image_base64", base64));
            }

            message.put("content", contentArray);
            inputArray.put(message);
            requestJson.put("input", inputArray);

            RequestBody body = RequestBody.create(
                    requestJson.toString(),
                    MediaType.parse("application/json")
            );

            OpenAIApi api = RetrofitClient.getClient();

            api.analyzeFile("Bearer " + API_KEY, body)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String val = response.body().string();
                                runOnUiThread(() -> reportOutput(val));
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
    private String encodeImageToBase64(String path) {
        try {
            File file = new File(path);
            byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
            return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void reportOutput(String string) {
        runOnUiThread(() -> {
            //   loadingContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            askGpt.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // 🔥 dati iniziali
        data.clear();

        try {
            JSONObject jsonObject = new JSONObject(string);

            String text = jsonObject
                    .getJSONArray("output")
                    .getJSONObject(0)
                    .getJSONArray("content")
                    .getJSONObject(0)
                    .getString("text");
            text = text.replace("```json", "")
                    .replace("```", "")
                    .trim();
            JSONObject innerJson = new JSONObject(text);

            // 👇 questo funziona anche se ci sono \n e \"
            // JSONObject innerJson = new JSONObject(text);

            //   textView.setText("");

            StringBuilder result = new StringBuilder();

            try {
                // JSONObject jsonObject = new JSONObject(string);

                Iterator<String> keys = innerJson.keys();

                while (keys.hasNext()) {
                    String key = keys.next();
                    System.out.println(key);
                    Object value = innerJson.optString(key);
                    setValues(key, value.toString(), data);
                    //      data.add(new RowModel(key,value.toString()));

                    //   textView.append(key + ":" + value.toString());

                }
                TableAdapter adapter = new TableAdapter(data);

                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void setValues(String key, String value, ArrayList<RowModel> data) {
        String s = key + " " + value;
        s = s.replace("\"", "");

        String[] coppie = s.split(",");
        for (String coppia : coppie) {
            int index = coppia.lastIndexOf(":");
            if (index == -1) {
                data.add(new RowModel(coppia, ""));
                continue;
            }
            index = coppia.lastIndexOf(":");
            if (index == -1) {
                data.add(new RowModel(key, value));
                continue;
            }
            String v1 = coppia.substring(0, index);
            String v2 = coppia.substring(index + 1);
            data.add(new RowModel(v1, v2));
        }

    }
}