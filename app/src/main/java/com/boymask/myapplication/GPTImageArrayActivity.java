package com.boymask.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boymask.RysLogger;
import com.boymask.myapplication.listaparametri.RowModel;
import com.boymask.myapplication.listaparametri.TableAdapter;
import com.boymask.myapplication.retrofit.ApiGpt;
import com.boymask.testpay.retrofit_boot.RetrofitBootClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GPTImageArrayActivity extends AppCompatActivity  {

    private ArrayList<RowModel> data = new ArrayList<>();
    private Button esci;
    private View progressBar;
    private View loadingText;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gptdata_reader);


        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);
        esci = findViewById(R.id.esci);
        View loadingContainer = findViewById(R.id.loadingContainer);
        recyclerView = findViewById(R.id.recyclerView);

// all'inizio mostro loading e nascondo lista
        loadingContainer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        esci.setVisibility(View.GONE);
        esci.setOnClickListener(v -> {

            finish();

        });
        ArrayList<String> paths = getIntent().getStringArrayListExtra("content");
        if (paths == null || paths.isEmpty()) {
            finish();
            return;
        }


            analyzeImagesRemote(paths);

    }

    private void analyzeImagesRemote(ArrayList<String> paths) {
        List<String> filesbase64 = new ArrayList<>();
        for (String path : paths)
            filesbase64.add( encodeImageToBase64(path));

        Map<String, Object> body = new HashMap<>();
        body.put("images", filesbase64);


        ApiGpt api = RetrofitBootClient.getClient().create(ApiGpt.class);



       api.analyze(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        String val = response.body().string();
                        runOnUiThread(() -> reportOutput(val));
                    }
                    if (response.errorBody() != null) {
                        String val = response.errorBody().string();
                        runOnUiThread(() -> reportOutput(val));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    RysLogger.add(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                RysLogger.add(t);
            }
        });
    }



    private String encodeImageToBase64(String path) {
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            fis.close();

            return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void reportOutput(String string) {
        runOnUiThread(() -> {
            //   loadingContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            esci.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
        });

        //     RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // 🔥 dati iniziali
        data.clear();

        try {
            try {
                JSONObject jsonObject = new JSONObject(string);

                JSONArray output = jsonObject.optJSONArray("output");
                if (output == null || output.length() == 0) return;

                JSONObject first = output.optJSONObject(0);
                if (first == null) return;

                JSONArray content = first.optJSONArray("content");
                if (content == null || content.length() == 0) return;

                JSONObject c0 = content.optJSONObject(0);
                if (c0 == null) return;

                String text = c0.optString("text");
                text = text.replace("```json", "")
                        .replace("```", "")
                        .trim();
                //    JSONObject innerJson = new JSONObject(text);
                setValues2(text, data);
            }catch(JSONException je){
                RysLogger.add(je);
                setValues2(string, data);
            }
            try {

                TableAdapter adapter = new TableAdapter(data);

                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
                RysLogger.add(e);

            }

        } catch (Exception e) {
            e.printStackTrace();
            RysLogger.add(e);
        }

    }

    private void setValues2(String value, ArrayList<RowModel> data) {


        data.add(new RowModel(value, ""));

    }
}