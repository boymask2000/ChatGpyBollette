package com.boymask.myapplication.listastoricobollette;

import com.boymask.myapplication.database.Bolletta;

import java.sql.Date;

public class RowStoricoBolletteModel {

    private final long id;
    private long data;



    public RowStoricoBolletteModel(Bolletta b) {
        this.id=b.uid;
        this.data = b.datap;
    }
    public long getData() {
        return data;
    }
    public long getId() {
        return id;
    }

}