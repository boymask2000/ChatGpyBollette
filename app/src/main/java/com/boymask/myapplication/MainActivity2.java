package com.boymask.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.boymask.RysLogger;
import com.boymask.testpay.retrofit_boot.PaymentApi;
import com.boymask.testpay.retrofit_boot.RetrofitBootClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity2 extends AppCompatActivity {


    //  private TextView username;
    public static Context context;

    public static String user;
    public static String stripeKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = UserHandler.getUsername(this);
        //  username = findViewById(R.id.username);
        //   username.setText(user);
        ImageButton image = findViewById(R.id.image);
        image.setOnClickListener(v -> {
            Intent intent2 = new Intent(MainActivity2.this, ImageTakerActivity.class);

            startActivity(intent2);
        });
        ImageButton foto = findViewById(R.id.foto);
        foto.setOnClickListener(v -> {
            Intent intent2 = new Intent(MainActivity2.this, FotoTakerActivity.class);

            startActivity(intent2);
        });


        ImageButton button = findViewById(R.id.button);
        TextView messaggio = findViewById(R.id.messaggio);

        ActivityResultLauncher<String> filePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {

                        String pathContenuto = leggiFile(uri);

                        Intent intent = new Intent(MainActivity2.this, GPTPDFArrayRemoteActivity.class);
                        ArrayList<String> vals = new ArrayList<>();
                        vals.add(pathContenuto);
                        intent.putStringArrayListExtra("content", vals);
                        startActivity(intent);


                    }
                }
        );

        ;

        button.setOnClickListener(v -> {

            filePicker.launch("*/*"); // puoi mettere "text/plain"
        });


        DBHandler.init(this);

        List<ImageButton> buttons = new ArrayList<>();
        buttons.add(button);
        buttons.add(foto);
        buttons.add(image);

        UserHandler.checkBolletteDisponibili(buttons, user, messaggio, this);

        getPayKey();
        //     buttons();
    }

    private void getPayKey() {

        PaymentApi api = RetrofitBootClient.getClient().create(PaymentApi.class);
        Call<Map<String, String>> call = api.getPayKey();

        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                Map<String, String> txt = response.body();
              //  Log.d("SERVER_RESPONSE", txt.get("key"));
                if (response.isSuccessful() && response.body() != null) {
                    stripeKey= response.body().get("key");

                } else {
                    Log.e("API", "Errore risposta: " + response.code());
                    RysLogger.add("Errore risposta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e("API", "Errore rete: " + t.getMessage());
                RysLogger.add("Errore rete: " + t.getMessage());
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        int id = item.getItemId();
        if (id == R.id.logger) {
            Intent intent = new Intent(MainActivity2.this, LoggerActivity.class);

            startActivity(intent);
            return true;
        }
        if (id == R.id.glossario) {
            Intent intent = new Intent(MainActivity2.this, GlossarioActivity.class);

            startActivity(intent);
            return true;
        }
        if (id == R.id.archivio) {
            Intent intent = new Intent(MainActivity2.this, StoricoBollettaActivity2.class);

            startActivity(intent);
            return true;
        }
        if (id == R.id.stato) {
            Intent intent = new Intent(MainActivity2.this, StatusActivity.class);

            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private String leggiFile(Uri uri) {
        Log.d("MainActivity2", "Reading file from URI: " + uri);
        File file = new File(getCacheDir(), "temp.pdf");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            InputStream is = getContentResolver().openInputStream(uri);

            if (is != null) {
                int nRead;
                byte[] data = new byte[4096];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                is.close();

                //Salvo i dati in un file temporaneo

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(buffer.toByteArray());
                fos.close();
            }
        } catch (Exception e) {
            Log.e("MainActivity2", "Error reading file", e);
        }

        return file.getAbsolutePath();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }
}
