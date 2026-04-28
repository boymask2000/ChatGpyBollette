package com.boymask.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ImageTakerActivity extends AppCompatActivity {


    private ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
     //   setContentView(R.layout.activity_image_taker);

      //  Button btnSelect = findViewById(R.id.btnSelect);

        pickMultipleMedia =
                registerForActivityResult(
                        new ActivityResultContracts.PickMultipleVisualMedia(5), // max 5 immagini
                        uris -> {
                            if (!uris.isEmpty()) {
                                Toast.makeText(this, "Selezionate: " + uris.size(), Toast.LENGTH_SHORT).show();

                                for (Uri uri : uris) {
                                    // QUI puoi usarle (upload, preview ecc.)
                                    System.out.println("URI: " + uri);
                                }
                            } else {
                                Toast.makeText(this, "Nessuna immagine selezionata", Toast.LENGTH_SHORT).show();
                            }
                        });
        openPicker();
       // btnSelect.setOnClickListener(v -> openPicker());
    }

    private void openPicker() {
        pickMultipleMedia.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()
        );
    }
}