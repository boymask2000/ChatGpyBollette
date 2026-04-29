package com.boymask;

import java.util.ArrayList;
import java.util.List;

public class RysLogger {

    private static List<String> msgs = new ArrayList<>();

    public static void init(){
        msgs.clear();
    }

    public static void add(String s ){
        msgs.add(s);
    }

    public static List<String> getMsgs() {
        return msgs;
    }
}
