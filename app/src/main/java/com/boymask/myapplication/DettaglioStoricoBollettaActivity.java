package com.boymask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.boymask.myapplication.database.Bolletta;
import com.boymask.myapplication.listasuggerimenti.TableSuggAdapter;

import java.util.ArrayList;
import java.util.List;

public class DettaglioStoricoBollettaActivity extends AppCompatActivity {

    private Button esci;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dettaglio_storico_bolletta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        esci = findViewById(R.id.esci);
        esci.setOnClickListener(v -> {

            DettaglioStoricoBollettaActivity.this.finish();

        });

        long id = getIntent().getLongExtra("id", -1);


        new Thread(() -> {
            Bolletta b = DBHandler.getStoricoBollettaById((int) id);
           
            runOnUiThread(() -> {
             //   analisi.setText(b.analisi);
                List<String> lista = new ArrayList<>();
                lista.add(b.analisi);
                TableSuggAdapter adapter = new TableSuggAdapter(lista);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
            });

        }).start();
    }
}