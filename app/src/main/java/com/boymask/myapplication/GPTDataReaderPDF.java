package com.boymask.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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


import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

    private ArrayList<RowModel> data = new ArrayList<>();
    private Button askGpt;
    private View loadingContainer;
    private View progressBar;
    private View loadingText;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gptdata_reader_pdf);

        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);
        askGpt = findViewById(R.id.askgpt);
        loadingContainer = findViewById(R.id.loadingContainer);
        recyclerView = findViewById(R.id.recyclerView);

// all'inizio mostro loading e nascondo lista
        loadingContainer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        askGpt.setVisibility(View.GONE);

        askGpt.setOnClickListener(v -> {


            Intent intent = new Intent(GPTDataReaderPDF.this, Suggester.class);
            intent.putExtra("datiBolletta", collectDataString());
            startActivity(intent);
        });
        String pathContent = getIntent().getStringExtra("content");
        processPdf(pathContent);
    }


    private void processPdf(String pathContent) {
        Uri uri = Uri.parse(pathContent);


        try {

            PdfTextExtractor.extract(this, uri, t -> {

                runOnUiThread(() -> reportOutput(t));

            });

             /*       runOnUiThread(() -> txtResult.setText("Analisi in corso..."));

                    callApi(prompt);*/

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> reportOutput("Errore PDF"));
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

    private String collectDataString() {
        StringBuilder out = new StringBuilder();
        for (RowModel m : data) {
            if (out.length() > 0) out.append(',');
            out.append(m.getLabel()).append(":").append(m.getValue());
        }
        return out.toString();
    }

}