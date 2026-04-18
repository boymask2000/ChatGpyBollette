package com.boymask.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity2 extends AppCompatActivity {

    public static  String API_KEY = "";

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
        API_KEY = Furbo.ketKey(this);

        ImageButton button = findViewById(R.id.button);

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
    }

    private String leggiFile(Uri uri) {
        System.out.println(uri);
        File file = new File(getCacheDir(), "temp.pdf");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            InputStream is = getContentResolver().openInputStream(uri);


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



        } catch (Exception e) {
            e.printStackTrace();

        }

        return file.getAbsolutePath();
    }
}
