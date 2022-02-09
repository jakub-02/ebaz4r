package com.example.eb4zar.model;

public class HodnotenieDetail {
    String text, uzivatel, uzivatelPridal, datumPridania, casPridania;
    float pocetHviezd;

    public HodnotenieDetail(){

    }

    public HodnotenieDetail(String text, String uzivatel, String uzivatelPridal, String datumPridania, String casPridania, float pocetHviezd) {
        this.text = text;
        this.uzivatel = uzivatel;
        this.uzivatelPridal = uzivatelPridal;
        this.datumPridania = datumPridania;
        this.casPridania = casPridania;
        this.pocetHviezd = pocetHviezd;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUzivatel() {
        return uzivatel;
    }

    public void setUzivatel(String uzivatel) {
        this.uzivatel = uzivatel;
    }

    public String getUzivatelPridal() {
        return uzivatelPridal;
    }

    public void setUzivatelPridal(String uzivatelPridal) {
        this.uzivatelPridal = uzivatelPridal;
    }

    public String getDatumPridania() {
        return datumPridania;
    }

    public void setDatumPridania(String datumPridania) {
        this.datumPridania = datumPridania;
    }

    public String getCasPridania() {
        return casPridania;
    }

    public void setCasPridania(String casPridania) {
        this.casPridania = casPridania;
    }

    public float getPocetHviezd() {
        return pocetHviezd;
    }

    public void setPocetHviezd(float pocetHviezd) {
        this.pocetHviezd = pocetHviezd;
    }
}
