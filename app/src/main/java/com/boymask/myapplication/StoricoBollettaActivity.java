package com.boymask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boymask.myapplication.database.Bolletta;
import com.boymask.myapplication.listastoricobollette.RowStoricoBolletteModel;
import com.boymask.myapplication.listastoricobollette.TableStoricoBolletteAdapter;
import com.boymask.myapplication.listasuggerimenti.TableSuggAdapter;

import java.util.ArrayList;
import java.util.List;

public class StoricoBollettaActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    Button esci ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_storico_bolletta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        esci = findViewById(R.id.Esci);
        esci.setOnClickListener(v -> {
          //  Intent myIntent2 = new Intent(getApplicationContext(), StoricoBollettaActivity.class);
            StoricoBollettaActivity.this.finish();

        });
        recyclerView = findViewById(R.id.recyclerView);

        buildListaRighe();
    }

    private void buildListaRighe() {
        new Thread(() -> {
            List<Bolletta> bollette = DBHandler.getStoricoBollette();

            List<RowStoricoBolletteModel> rows = buildRows(bollette);
            runOnUiThread(() -> {
            TableStoricoBolletteAdapter adapter = new TableStoricoBolletteAdapter(rows);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter); });
        }).start();
    }

    private List<RowStoricoBolletteModel> buildRows(List<Bolletta> bollette) {
        List<RowStoricoBolletteModel> rows = new ArrayList<>();
        for (Bolletta b : bollette) {
            rows.add(new RowStoricoBolletteModel(b));
        }
        return rows;
    }
}