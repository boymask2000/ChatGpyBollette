package com.boymask;


import java.util.Date;


public class User {

    private Long id;

    private String userid;

    private String abbonamento;
    private int bolletteTotali;
    private int bolletteAnalizzate;

    private String lastAccess;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAbbonamento() {
        return abbonamento;
    }

    public void setAbbonamento(String abbonamento) {
        this.abbonamento = abbonamento;
    }

    public int getBolletteTotali() {
        return bolletteTotali;
    }

    public void setBolletteTotali(int bolletteTotali) {
        this.bolletteTotali = bolletteTotali;
    }

    public int getBolletteAnalizzate() {
        return bolletteAnalizzate;
    }

    public void setBolletteAnalizzate(int bolletteAnalizzate) {
        this.bolletteAnalizzate = bolletteAnalizzate;
    }

    public String getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(String lastAccess) {
        this.lastAccess = lastAccess;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", userid=" + userid + ", abbonamento=" + abbonamento + ", bolletteTotali="
                + bolletteTotali + ", bolletteAnalizzate=" + bolletteAnalizzate + ", lastAccess=" + lastAccess + "]";
    }

}

