package com.example.eb4zar.model;

public class ProductDetail {
    private String nazov, popis, cena, uzivatel, fotka, kategoria, datumpridania, caspridania;
    private int poradie;

    public ProductDetail(){

    }

    public String getNazov() {
        return nazov;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }

    public String getCena() {
        return cena;
    }

    public void setCena(String cena) {
        this.cena = cena;
    }

    public String getUzivatel() {
        return uzivatel;
    }

    public void setUzivatel(String uzivatel) {
        this.uzivatel = uzivatel;
    }

    public String getFotka() {
        return fotka;
    }

    public void setFotka(String fotka) {
        this.fotka = fotka;
    }

    public String getKategoria() {
        return kategoria;
    }

    public void setKategoria(String kategoria) {
        this.kategoria = kategoria;
    }

    public String getDatumpridania() {
        return datumpridania;
    }

    public void setDatumpridania(String datumpridania) {
        this.datumpridania = datumpridania;
    }

    public String getCaspridania() {
        return caspridania;
    }

    public void setCaspridania(String caspridania) {
        this.caspridania = caspridania;
    }

    public int getPoradie() {
        return poradie;
    }

    public void setPoradie(int poradie) {
        this.poradie = poradie;
    }
}
