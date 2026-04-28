package com.boymask.myapplication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class MainActivity2 extends AppCompatActivity {

    public static String API_KEY = "";
    private TextView username;
private Button image ;
    private Button foto ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String user = UserHandler.getUsername(this);
        username = findViewById(R.id.username);
        //   username.setText(user);
        image= findViewById(R.id.image);
        image.setOnClickListener(v -> {
            Intent intent2 = new Intent(MainActivity2.this, ImageTakerActivity.class);

            startActivity(intent2);
        });
        foto= findViewById(R.id.foto);
        foto.setOnClickListener(v -> {
            Intent intent2 = new Intent(MainActivity2.this, FotoTakerActivity.class);

            startActivity(intent2);
        });
/*

        Intent intent2 = new Intent(MainActivity2.this, FirstUseActivity.class);

        startActivity(intent2);
*/


        API_KEY = Furbo.ketKey(this);


        ImageButton button = findViewById(R.id.button);
        TextView messaggio = findViewById(R.id.messaggio);

        ActivityResultLauncher<String> filePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        String pathContenuto = leggiFile(uri);
                        //   textView.setText(contenuto);
                        Intent intent = new Intent(MainActivity2.this, GPTDataReader.class);
                        intent.putExtra("content", pathContenuto);
                        startActivity(intent);
                    }
                }
        );

        button.setOnClickListener(v -> {
            filePicker.launch("*/*"); // puoi mettere "text/plain"
        });



        DBHandler.init(this);

        UserHandler.checkBolletteDisponibili(button, user, messaggio, this);

   //     buttons();
    }

    public String getUsername2() {
        String androidId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        return androidId;
    }

    private void buttons() {
        UserHandler.decBollette();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        int id = item.getItemId();
        if (id == R.id.glossario) {
            Intent intent = new Intent(MainActivity2.this, GlossarioActivity.class);

            startActivity(intent);
            return true;
        }
        if (id == R.id.archivio) {
            Intent intent = new Intent(MainActivity2.this, StoricoBollettaActivity.class);

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
