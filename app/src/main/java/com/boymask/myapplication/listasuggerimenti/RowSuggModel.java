package com.boymask.myapplication.listasuggerimenti;

public class RowSuggModel {

    private String label;   // colonna fissa

    public RowSuggModel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}