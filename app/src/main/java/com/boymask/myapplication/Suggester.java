package com.boymask.myapplication;

import android.content.Intent;
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

import com.boymask.UpdaterToken;
import com.boymask.myapplication.database.Bolletta;
import com.boymask.myapplication.listaparametri.TableAdapter;
import com.boymask.myapplication.listasuggerimenti.TableSuggAdapter;
import com.boymask.myapplication.retrofit.OpenAIApi;
import com.boymask.myapplication.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Suggester extends AppCompatActivity {
    String API_KEY = MainActivity2.API_KEY;
    private View loadingContainer;
    private View progressBar;
    private View loadingText;
    private RecyclerView recyclerView;
    private Bolletta bolletta = new Bolletta();

    //  private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggester);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //  textView = findViewById(R.id.textView);
        Button esci = findViewById(R.id.Esci);
        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);
        recyclerView = findViewById(R.id.recyclerView);
        String datiBolletta = getIntent().getStringExtra("datiBolletta");
        bolletta.dati = datiBolletta;
        //   loadingContainer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        esci.setOnClickListener(v -> {

            Suggester.this.finish();

        });
        askGpt(datiBolletta);
    }

    private void askGpt(String datiBolletta) {

        OpenAIApi api = RetrofitClient.getClient();
        try {
            JSONObject requestJson = new JSONObject();

            requestJson.put("model", Prompt.MODEL);

            JSONArray inputArray = new JSONArray();

            JSONObject message = new JSONObject();
            message.put("role", "user");

            JSONArray contentArray = new JSONArray();

// testo richiesta
            contentArray.put(new JSONObject()
                    .put("type", "input_text")
                    //     .put("text", "Estrai nome, data, importo e codice fiscale in formato JSON"));
                    .put("text", Prompt.PROMPT_ASK + ":" + datiBolletta));


            message.put("content", contentArray);
            inputArray.put(message);
            System.out.println(contentArray);
            requestJson.put("input", inputArray);
            RequestBody body = RequestBody.create(
                    requestJson.toString(),
                    MediaType.parse("application/json")
            );
            api.analyzeFile("Bearer " + API_KEY, body)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                if (response.isSuccessful()) {
                                    System.out.println("RISULTATO:");

                                    String val = response.body().string();
                                    System.out.println(val);
                                    //  FileUtil.saveToDisk(val);
                                    runOnUiThread(() -> reportOutput(val));
                                } else {
                                    System.out.println("Errore analisi");
                                    String val = response.errorBody().string();
                                    System.out.println(val);
                                    runOnUiThread(() -> reportOutput(val));
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

    private void reportOutput(String string) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        runOnUiThread(() -> {
            //   loadingContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);


            progressBar.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
        });

        try {
            JSONObject jsonObject = new JSONObject(string);
            getTokens(jsonObject);
            String analisi = jsonObject
                    .getJSONArray("output")
                    .getJSONObject(0)
                    .getJSONArray("content")
                    .getJSONObject(0)
                    .getString("text");
            analisi = analisi.replace("```json", "")
                    .replace("```", "")
                    .trim();

            bolletta.analisi = analisi;

            // textView.setText(text);
            List<String> lista = buildListaRighe(analisi);


            TableSuggAdapter adapter = new TableSuggAdapter(lista);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            DBHandler.saveBolletta(bolletta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTokens(JSONObject jsonObject) throws JSONException {
        String tokens = JsonReader.getTokens(jsonObject);
        UpdaterToken.update(Long.parseLong(tokens), true);
    }

    private List<String> buildListaRighe(String text) {
        ArrayList<String> v = new ArrayList<>();
        v.add(text);
        return v;
    }
}