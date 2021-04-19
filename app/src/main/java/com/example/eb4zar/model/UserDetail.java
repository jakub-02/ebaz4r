package com.example.eb4zar.model;

public class UserDetail {
    private String meno;
    private String priezvisko;
    private String telefon;

    public UserDetail(){

    }

    public UserDetail(String meno, String priezvisko, String telefon){
        this.meno = meno;
        this.priezvisko = priezvisko;
        this.telefon = telefon;
    }

    public String getMeno() {
        return meno;
    }

    public void setMeno(String meno) {
        this.meno = meno;
    }

    public String getPriezvisko() {
        return priezvisko;
    }

    public void setPriezvisko(String priezvisko) {
        this.priezvisko = priezvisko;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
}
