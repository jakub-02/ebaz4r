package com.example.eb4zar.model;

public class Kategoria {
    String nazov_kategorie;
    int fotka_kategorie;

    public Kategoria(String nazov, int fotka) {
        this.nazov_kategorie = nazov;
        this.fotka_kategorie = fotka;
    }

    public String getNazov_kategorie() {
        return nazov_kategorie;
    }

    public void setNazov_kategorie(String nazov_kategorie) {
        this.nazov_kategorie = nazov_kategorie;
    }

    public int getFotka_kategorie() {
        return fotka_kategorie;
    }

    public void setFotka_kategorie(int fotka_kategorie) {
        this.fotka_kategorie = fotka_kategorie;
    }
}
