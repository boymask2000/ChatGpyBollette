package com.boymask.myapplication;

public class PromptBuilder {

    public static String build(String text) {
        return "Analizza questa bolletta luce/gas.\n" +
                "Estrai SOLO i seguenti campi in formato JSON:\n\n" +
                "{\n" +
                "  \"intestatario\": \"\",\n" +
                "  \"codiceCliente\": \"\",\n" +
                "  \"indirizzo\": \"\",\n" +
                "  \"fornitore\": \"\",\n" +
                "  \"totale\": \"\",\n" +
                "  \"consumoSmc\": \"\",\n" +
                "  \"periodo\": \"\"\n" +
                "}\n\n" +
                "Se un campo non esiste metti stringa vuota.\n" +
                "Rispondi SOLO con JSON valido.\n\n" +
                "TESTO:\n" + text;
    }
}