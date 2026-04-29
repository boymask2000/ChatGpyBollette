package com.boymask.myapplication;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boymask.RysLogger;
import com.boymask.myapplication.listasuggerimenti.TableSuggAdapter;

import java.util.List;

public class LoggerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logger);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button esci = findViewById(R.id.Esci);
        esci.setOnClickListener(v -> {

            LoggerActivity.this.finish();

        });
        Button clear = findViewById(R.id.clear);
        clear.setOnClickListener(v -> {

            RysLogger.init();

        });

        recyclerView = findViewById(R.id.recyclerView);

        List<String> msgs = RysLogger.getMsgs();

        TableSuggAdapter adapter = new TableSuggAdapter(msgs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}