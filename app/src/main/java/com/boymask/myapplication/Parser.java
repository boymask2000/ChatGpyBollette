package com.boymask.myapplication;

import org.json.JSONArray;
import org.json.JSONObject;

public class Parser {

    public static BollettaData parse(String jsonString) throws Exception {

        JSONObject root = new JSONObject(jsonString);
        JSONArray output = root.getJSONArray("output");

        JSONObject first = output.getJSONObject(0);
        JSONArray content = first.getJSONArray("content");

        String text = "";

        for (int i = 0; i < content.length(); i++) {
            JSONObject item = content.getJSONObject(i);

            if ("output_text".equals(item.getString("type"))) {
                text += item.getString("text");
            }
        }

        JSONObject data = new JSONObject(text);

        BollettaData b = new BollettaData();
        b.intestatario = data.optString("intestatario");
        b.codiceCliente = data.optString("codiceCliente");
        b.indirizzo = data.optString("indirizzo");
        b.fornitore = data.optString("fornitore");
        b.totale = data.optString("totale");
        b.consumoSmc = data.optString("consumoSmc");
        b.periodo = data.optString("periodo");

        return b;
    }
}