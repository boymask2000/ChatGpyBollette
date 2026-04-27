package com.boymask;

public class UpdaterTokenBean {
    private long token ;
    private boolean incBollette;

    public UpdaterTokenBean(long token, boolean incBollette) {
        super();
        this.token = token;
        this.incBollette = incBollette;
    }

    public long getToken() {
        return token;
    }
    public boolean isIncBollette() {
        return incBollette;
    }


}
