package com.boymask.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GPTDataReader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gptdata_reader);

        String pathContent = getIntent().getStringExtra("content");

        TextView textView = findViewById(R.id.textView);

   //     String testo = getIntent().getStringExtra("testo");
        textView.setText(pathContent);
    }
}