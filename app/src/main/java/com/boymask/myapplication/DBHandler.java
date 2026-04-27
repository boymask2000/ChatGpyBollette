package com.boymask.myapplication;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


import com.boymask.myapplication.database.Bolletta;
import com.boymask.myapplication.database.BollettaDao;
import com.boymask.myapplication.database.BollettaDatabase;
import com.boymask.myapplication.database.Status;
import com.boymask.myapplication.database.StatusDao;
import com.boymask.myapplication.database.StatusDatabase;

import java.util.List;

public class DBHandler {
    public static StatusDatabase dbStatus;
    public static BollettaDatabase dbBolletta;
    private static boolean inited = false;
    static final Migration MIGRATION_2_4 = new Migration(2, 1) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            // fai niente → schema ricreato da zero (ma senza crash)
        }
    };

    public static void init(Context context) {

        dbStatus = Room.databaseBuilder(context,
                        StatusDatabase.class, "database-name")
                .fallbackToDestructiveMigration(true)
                .build();

        dbBolletta = Room.databaseBuilder(context,
                        BollettaDatabase.class, "database-bo")
                .addMigrations(MIGRATION_2_4)
                .build();

        StatusDao statusDao = dbStatus.statusDao();


        new Thread(() -> {

            List<Status> stats = dbStatus.statusDao().getAll();

            if (stats.size() == 0) {
                initStatus();
            }
            inited = true;
        }).start();
    }

    private static Status initStatus() {
        StatusDao statusDao = dbStatus.statusDao();

        Status s = new Status();
        s.abbonamento = "FREE";
        s.bollette_disponibili = 2;
        s.bollette_fatte = 0;
        statusDao.insertAll(s);

        return s;
    }

    public static void checkBolletteDisponibili(ImageButton button, TextView messaggio, Activity context) {
        new Thread(() -> {
            Status status;

            while (!inited) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }


            List<Status> stats = dbStatus.statusDao().getAll();
            if (stats.isEmpty())
                status = initStatus();
            else
                status = stats.get(0);
            if (status.bollette_disponibili == 0) {
                context.runOnUiThread(() -> {
                    button.setEnabled(false);
                    messaggio.setText("Hai esaurito il numero di bollette disponibili");
                });
            }
        }).start();
    }

    public static List<Bolletta> getStoricoBollette() {
        BollettaDao bollettaDao = dbBolletta.bollettaDao();
        return bollettaDao.getAll();
    }

    public static void saveBolletta(Bolletta bolletta) {
        new Thread(() -> {
            BollettaDao bollettaDao = dbBolletta.bollettaDao();
            bollettaDao.insertAll(bolletta);

        }).start();
    }

    public static Bolletta getStoricoBollettaById( int id ){
        BollettaDao bollettaDao = dbBolletta.bollettaDao();
        return bollettaDao.getById(id);
    }
}
