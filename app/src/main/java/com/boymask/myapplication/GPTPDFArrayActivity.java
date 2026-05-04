package com.boymask.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boymask.UpdaterToken;
import com.boymask.myapplication.listaparametri.RowModel;
import com.boymask.myapplication.listaparametri.TableAdapter;
import com.boymask.myapplication.retrofit.OpenAIApi;
import com.boymask.myapplication.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GPTPDFArrayActivity extends AppCompatActivity {
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


            Intent intent = new Intent(GPTPDFArrayActivity.this, Suggester.class);
            intent.putExtra("datiBolletta", collectDataString());
            startActivity(intent);
        });



        ArrayList<String> paths = getIntent().getStringArrayListExtra("content");
        uploadFilesSequentially(paths, 0, new ArrayList<>());

        //     textView = findViewById(R.id.textView);

        //     String testo = getIntent().getStringExtra("testo");

  //      callChatGpt(pathContent);

    }
    private void uploadFilesSequentially(ArrayList<String> paths, int index, ArrayList<String> fileIds) {

        if (index >= paths.size()) {
            // tutti caricati → analizza
            analyzeMultipleFiles(RetrofitClient.getClient(), API_KEY, fileIds);
            return;
        }

        File file = new File(paths.get(index));

        RequestBody requestFile =
                RequestBody.create(file, MediaType.parse("application/octet-stream"));

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        RequestBody purpose =
                RequestBody.create("assistants", MediaType.parse("text/plain"));

        OpenAIApi api = RetrofitClient.getClient();

        api.uploadFile("Bearer " + API_KEY, body, purpose)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (!response.isSuccessful()) {
                                System.out.println("Errore upload");
                                return;
                            }

                            JSONObject json = new JSONObject(response.body().string());
                            String fileId = json.getString("id");

                            fileIds.add(fileId);

                            // continua con il prossimo file
                            uploadFilesSequentially(paths, index + 1, fileIds);

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
    private void analyzeMultipleFiles(OpenAIApi api, String apiKey, ArrayList<String> fileIds) {

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
                    .put("text", Prompt.PROMPT_ASK));

            // 👇 aggiungi TUTTI i file
            for (String fileId : fileIds) {
                contentArray.put(new JSONObject()
                        .put("type", "input_file")
                        .put("file_id", fileId));
            }

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
                                String val = response.body().string();
                                runOnUiThread(() -> reportOutput(val, false));
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
    private String collectDataString() {
        StringBuilder out = new StringBuilder();
        for (RowModel m : data) {
            if (out.length() > 0) out.append(',');
            out.append(m.getLabel()).append(":").append(m.getValue());
        }
        return out.toString();
    }



    private void reportOutput(String string, boolean incBoll) {
        data.clear();
        runOnUiThread(() -> {
            //   loadingContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            askGpt.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        try {
            JSONObject jsonObject = new JSONObject(string);

            getTokens(jsonObject, incBoll);
            String text = jsonObject
                    .getJSONArray("output")
                    .getJSONObject(0)
                    .getJSONArray("content")
                    .getJSONObject(0)
                    .getString("text");
            text = text.replace("```json", "")
                    .replace("```", "")
                    .trim();
            System.out.println( text);

            try {
                setValues("S", text, data);

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

    private void getTokens(JSONObject jsonObject, boolean inc) throws JSONException {
        String tokens = JsonReader.getTokens(jsonObject);

        UpdaterToken.update(Long.parseLong(tokens), inc);
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