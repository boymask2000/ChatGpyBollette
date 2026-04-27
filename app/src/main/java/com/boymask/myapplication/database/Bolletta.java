package com.boymask.myapplication.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;


@Entity
public class Bolletta {
    public Bolletta() {
        this.datap = System.currentTimeMillis();
    }

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "nome_file")
    public String nome_file;

    @ColumnInfo(name = "cont_bolletta")
    public byte[] cont_bolletta;

    @ColumnInfo(name = "analisi")
    public String analisi;

    @ColumnInfo(name = "dati")
    public String dati;


    @ColumnInfo(name = "data")
    public long datap;

}