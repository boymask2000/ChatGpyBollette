package com.boymask.myapplication.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Bolletta.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class BollettaDatabase extends RoomDatabase {
    public abstract BollettaDao bollettaDao();
}
