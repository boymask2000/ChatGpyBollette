package com.boymask.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BollettaDao {

    @Query("SELECT * FROM Bolletta order by data desc")
    List<Bolletta> getAll();

    @Insert
    void insertAll(Bolletta... bollette);
}