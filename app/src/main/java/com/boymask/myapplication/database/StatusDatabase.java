package com.boymask.myapplication.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Status.class}, version = 2)
public abstract class StatusDatabase extends RoomDatabase {
    public abstract StatusDao statusDao();
}
