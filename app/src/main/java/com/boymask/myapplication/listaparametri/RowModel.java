package com.boymask.myapplication.listaparametri;

public class RowModel {

    private String label;   // colonna fissa
    private String value;   // colonna editabile

    public RowModel(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}