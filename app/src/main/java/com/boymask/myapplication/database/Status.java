package com.boymask.myapplication.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Status {

    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "abbonamento")
    public String abbonamento;

    @ColumnInfo(name = "bollette_fatte")
    public int bollette_fatte;

    @ColumnInfo(name = "bollette_disponibili")
    public int bollette_disponibili;

    @ColumnInfo(name = "totale_token")
    public int totale_token;

}