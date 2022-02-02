package com.example.eb4zar.model;

public class UserDetail {
    private String meno;
    private String priezvisko;
    private String telefon;
    private String mail;
    private String fotka;

    public UserDetail(){
    }

    public UserDetail(String meno, String priezvisko, String telefon, String mail, String fotka){
        this.meno = meno;
        this.priezvisko = priezvisko;
        this.telefon = telefon;
        this.mail = mail;
        this.fotka = fotka;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getFotka() {
        return fotka;
    }

    public void setFotka(String fotka) {
        this.fotka = fotka;
    }
}
