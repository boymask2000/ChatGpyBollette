package com.boymask.myapplication;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.room.Room;

import com.boymask.myapplication.database.AppDatabase;
import com.boymask.myapplication.database.Status;
import com.boymask.myapplication.database.StatusDao;

import java.util.List;

public class DBHandler {
    public static AppDatabase db;

    public static void init(Context context) {
        db = Room.databaseBuilder(context,
                        AppDatabase.class, "database-name")
                .fallbackToDestructiveMigration(true)
                .build();

        StatusDao statusDao = db.statusDao();


        new Thread(() -> {
            List<Status> stats = db.statusDao().getAll();

            if (stats.size() == 0) {
                Status s = new Status();
                s.abbonamento = "FREE";
                s.bollette_disponibili = 2;
                s.bollette_fatte = 0;
                statusDao.insertAll(s);
            }
        }).start();
    }

    public static void checkBolletteDispnibili(ImageButton button, TextView messaggio, Activity context) {
        new Thread(() -> {
            List<Status> stats = db.statusDao().getAll();
            Status status = stats.get(0);
            if (status.bollette_disponibili == 0) {
                context.runOnUiThread(() -> {
                    button.setEnabled(false);
                    messaggio.setText("Hai esaurito il numero di bollette disponibili");
                });
            }
        }).start();
    }
}
