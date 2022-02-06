package com.example.eb4zar.model;

public class UserDetail {
    private String meno;
    private String priezvisko;
    private String telefon;
    private String mail;
    private String fotka;
    private int inzeraty;
    private int hodnotenia;

    public UserDetail(){
    }

    public UserDetail(String meno, String priezvisko, String telefon, String mail, String fotka, int inzeraty, int hodnotenia){
        this.meno = meno;
        this.priezvisko = priezvisko;
        this.telefon = telefon;
        this.mail = mail;
        this.fotka = fotka;
        this.inzeraty = inzeraty;
        this.hodnotenia = hodnotenia;
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

    public int getInzeraty() {
        return inzeraty;
    }

    public void setInzeraty(int inzeraty) {
        this.inzeraty = inzeraty;
    }

    public int getHodnotenia() {
        return hodnotenia;
    }

    public void setHodnotenia(int hodnotenia) {
        this.hodnotenia = hodnotenia;
    }
}
