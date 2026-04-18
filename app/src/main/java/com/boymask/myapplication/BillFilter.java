package com.boymask.myapplication;

public class BillFilter {

    public static String clean(String text) {

        StringBuilder out = new StringBuilder();

        String[] keywords = {
                "totale", "consumo", "fattura", "smc",
                "kwh", "fornitore", "cliente",
                "importo", "pagamento", "periodo"
        };

        for (String line : text.split("\n")) {

            String lower = line.toLowerCase();

            for (String k : keywords) {
                if (lower.contains(k)) {
                    out.append(line).append("\n");
                    break;
                }
            }
        }

        if (out.length() < 500) {
            return text.substring(0, Math.min(3000, text.length()));
        }

        return out.toString();
    }
}