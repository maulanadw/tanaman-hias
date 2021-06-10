package com.maulanadw.tanamanhias;

public class ModelProduct {
    private String idProduk, namaProduk, deskripsiProduk, kategoriProduk, jumlahProduk, iconProduk, hargaUtama, hargaDiskon, diskon, diskonTersedia, timeStamp, uid;

    public ModelProduct() {

    }

    public ModelProduct(String idProduk, String namaProduk, String deskripsiProduk, String kategoriProduk, String jumlahProduk, String iconProduk, String hargaUtama, String hargaDiskon, String diskon, String diskonTersedia, String timeStamp, String uid) {
        this.idProduk = idProduk;
        this.namaProduk = namaProduk;
        this.deskripsiProduk = deskripsiProduk;
        this.kategoriProduk = kategoriProduk;
        this.jumlahProduk = jumlahProduk;
        this.iconProduk = iconProduk;
        this.hargaUtama = hargaUtama;
        this.hargaDiskon = hargaDiskon;
        this.diskon = diskon;
        this.diskonTersedia = diskonTersedia;
        this.timeStamp = timeStamp;
        this.uid = uid;
    }

    public String getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(String idProduk) {
        this.idProduk = idProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public String getDeskripsiProduk() {
        return deskripsiProduk;
    }

    public void setDeskripsiProduk(String deskripsiProduk) {
        this.deskripsiProduk = deskripsiProduk;
    }

    public String getKategoriProduk() {
        return kategoriProduk;
    }

    public void setKategoriProduk(String kategoriProduk) {
        this.kategoriProduk = kategoriProduk;
    }

    public String getJumlahProduk() {
        return jumlahProduk;
    }

    public void setJumlahProduk(String jumlahProduk) {
        this.jumlahProduk = jumlahProduk;
    }

    public String getIconProduk() {
        return iconProduk;
    }

    public void setIconProduk(String iconProduk) {
        this.iconProduk = iconProduk;
    }

    public String getHargaUtama() {
        return hargaUtama;
    }

    public void setHargaUtama(String hargaUtama) {
        this.hargaUtama = hargaUtama;
    }

    public String getHargaDiskon() {
        return hargaDiskon;
    }

    public void setHargaDiskon(String hargaDiskon) {
        this.hargaDiskon = hargaDiskon;
    }

    public String getDiskon() {
        return diskon;
    }

    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }

    public String getDiskonTersedia() {
        return diskonTersedia;
    }

    public void setDiskonTersedia(String diskonTersedia) {
        this.diskonTersedia = diskonTersedia;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
