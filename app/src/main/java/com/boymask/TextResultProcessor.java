package com.boymask;

public class TextResultProcessor {

    public static String process( String text ){
        String addon = "Se hai dei dubbi su qualche termine usato nella tua bolletta puoi fare riferimento al Glossario che trovi nel menù in alto a destra";

        return text+ "\n\n"+addon;
    }
}
