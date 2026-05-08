package com.boymask.myapplication;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boymask.RysLogger;
import com.boymask.myapplication.listaparametri.RowModel;
import com.boymask.myapplication.listaparametri.TableAdapter;
import com.boymask.myapplication.retrofit.ApiGpt;
import com.boymask.testpay.retrofit_boot.RetrofitBootClient;

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

public class GPTPDFArrayRemoteActivity extends AppCompatActivity {

    private ArrayList<RowModel> data = new ArrayList<>();
    private Button esci;
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
        esci = findViewById(R.id.esci);
        loadingContainer = findViewById(R.id.loadingContainer);
        recyclerView = findViewById(R.id.recyclerView);

// all'inizio mostro loading e nascondo lista
        loadingContainer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        esci.setVisibility(View.GONE);
        esci.setOnClickListener(v -> {

            finish();

        });



        ArrayList<String> paths = getIntent().getStringArrayListExtra("content");
        analyzeMultipleFiles(paths);

        //     textView = findViewById(R.id.textView);

        //     String testo = getIntent().getStringExtra("testo");

        //      callChatGpt(pathContent);

    }
    private void analyzeMultipleFiles(ArrayList<String> paths) {

        List<String> filesbase64 = new ArrayList<>();
        for (String path : paths)
            filesbase64.add( encodeImageToBase64(path));

        Map<String, Object> body = new HashMap<>();
        body.put("pdf", filesbase64);
        body.put("user", MainActivity2.user);

        ApiGpt api = RetrofitBootClient.getClient().create(ApiGpt.class);


        api.analyzepdf(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        String val = response.body().string();
                        runOnUiThread(() -> reportOutput(val));
                    }
                    if (response.errorBody() != null) {
                        String val = response.errorBody().string();
                        mostraPopup(val);
                        //  runOnUiThread(() -> reportOutput(val));
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
    private String collectDataString() {
        StringBuilder out = new StringBuilder();
        for (RowModel m : data) {
            if (out.length() > 0) out.append(',');
            out.append(m.getLabel()).append(":").append(m.getValue());
        }
        return out.toString();
    }

    private void mostraPopup(String messaggio) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Messaggio");
        builder.setMessage(messaggio);

        builder.setPositiveButton("Esci", (dialog, which) -> {
            finish(); // chiude l'activity
        });

        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void reportOutput(String string) {
        data.clear();
        runOnUiThread(() -> {
            //   loadingContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            esci.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

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
}