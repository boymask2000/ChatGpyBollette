package com.boymask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
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
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GPTDataReader extends AppCompatActivity {
    String API_KEY = MainActivity2.API_KEY;
    private ArrayList<RowModel> data = new ArrayList<>();
    private Button askGpt;
    private View loadingContainer;
    private View progressBar;
    private View loadingText;
    private RecyclerView recyclerView;
    //  private TextView textView;

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


        askGpt.setOnClickListener(v -> {


            Intent intent = new Intent(GPTDataReader.this, Suggester.class);
            intent.putExtra("datiBolletta", collectDataString());
            startActivity(intent);
        });


        String pathContent = getIntent().getStringExtra("content");

        //     textView = findViewById(R.id.textView);

        //     String testo = getIntent().getStringExtra("testo");

        callChatGpt(pathContent);

    }

    private String collectDataString() {
        StringBuilder out = new StringBuilder();
        for (RowModel m : data) {
            if (out.length() > 0) out.append(',');
            out.append(m.getLabel()).append(":").append(m.getValue());
        }
        return out.toString();
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
        api.uploadFile("Bearer " + API_KEY, body, purpose)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (!response.isSuccessful()) {
                                System.out.println("Upload error " + response);
                                System.out.println(response.errorBody().string());
                                reportOutput(response.errorBody().string());
                                return;
                            }

                            String res = response.body().string();
                            //      reportOutput(res);
                            JSONObject json = new JSONObject(res);

                            String fileId = json.getString("id");

                            System.out.println("File caricato: " + fileId);

                            // STEP 2: ANALISI
                            analyze(api, API_KEY, fileId);

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

    private void analyze(OpenAIApi api, String apiKey, String fileId) {

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
                    .put("text", Prompt.PROMPT));

// file tramite file_id
            contentArray.put(new JSONObject()
                    .put("type", "input_file")
                    .put("file_id", fileId));

            message.put("content", contentArray);
            inputArray.put(message);
            System.out.println(contentArray);
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
        data.clear();
        runOnUiThread(() -> {
            //   loadingContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            askGpt.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // 🔥 dati iniziali


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