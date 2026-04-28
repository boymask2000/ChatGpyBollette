package com.boymask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.boymask.User;
import com.boymask.myapplication.database.Status;
import com.boymask.myapplication.database.StatusDao;

import java.io.IOException;
import java.util.List;

public class StatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_status);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        handleButtons();

        TextView abbonamento = findViewById(R.id.abbonamento);
        TextView immesse = findViewById(R.id.immesse);
        TextView disonibili = findViewById(R.id.disonibili);
        new Thread(() -> {
            try {
                User user = UserHandler.getUser();

                runOnUiThread(() -> {
                    abbonamento.setText(user.getAbbonamento());
                    immesse.setText(String.valueOf(user.getBolletteAnalizzate()));
                    disonibili.setText(String.valueOf(user.getBolletteTotali() - user.getBolletteAnalizzate()));
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }).start();
/*        StatusDao statusDao = DBHandler.dbStatus.statusDao();

        new Thread(() -> {
            List<Status> st = statusDao.getAll();
            if (st.size() == 0) {
                return;
            }
            Status status = st.get(0);
            runOnUiThread(() -> {
                abbonamento.setText(status.abbonamento);
                immesse.setText(String.valueOf(status.bollette_fatte));
                disonibili.setText(String.valueOf(status.bollette_disponibili));
            });
        }).start();*/
    }

    private void handleButtons() {
        Button handle_abbonamento = findViewById(R.id.handle_abbonamento);
        Button esci = findViewById(R.id.esci);

        esci.setOnClickListener(v -> {

            StatusActivity.this.finish();
        });
        handle_abbonamento.setOnClickListener(v -> {
            Intent intent = new Intent(StatusActivity.this, MainPayActivity.class);

            startActivity(intent);
        });
    }
}