package com.boymask.myapplication.listastoricobollette;

import com.boymask.myapplication.database.Bolletta;

import java.sql.Date;

public class RowStoricoBolletteModel {

    private final int id;
    private long data;



    public RowStoricoBolletteModel(Bolletta b) {
        this.id=b.uid;
        this.data = b.datap;
    }
    public long getData() {
        return data;
    }
    public int getId() {
        return id;
    }

}