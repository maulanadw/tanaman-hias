package com.maulanadw.tanamanhias;

public class ModelCartItem {

    String id, pid, nama, harga, cost, jumlah;

    public ModelCartItem() {
    }

    public ModelCartItem(String id, String pid, String nama, String harga, String cost, String jumlah) {
        this.id = id;
        this.pid = pid;
        this.nama = nama;
        this.harga = harga;
        this.cost = cost;
        this.jumlah = jumlah;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }
}
