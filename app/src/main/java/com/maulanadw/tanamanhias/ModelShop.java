package com.maulanadw.tanamanhias;

public class ModelShop {
    private String uid, email, nama, namaToko, noHp, biayaPengiriman, provinsi, kabupaten, kota, alamat, latitude, longitude, timeStamp, tipeAkun, online, bukaToko, gambarProfil;

    public ModelShop() {

    }

    public ModelShop(String uid, String email, String nama, String namaToko, String noHp, String biayaPengiriman, String provinsi, String kabupaten, String kota, String alamat, String latitude, String longitude, String timeStamp, String tipeAkun, String online, String bukaToko, String gambarProfil) {
        this.uid = uid;
        this.email = email;
        this.nama = nama;
        this.namaToko = namaToko;
        this.noHp = noHp;
        this.biayaPengiriman = biayaPengiriman;
        this.provinsi = provinsi;
        this.kabupaten = kabupaten;
        this.kota = kota;
        this.alamat = alamat;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
        this.tipeAkun = tipeAkun;
        this.online = online;
        this.bukaToko = bukaToko;
        this.gambarProfil = gambarProfil;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNamaToko() {
        return namaToko;
    }

    public void setNamaToko(String namaToko) {
        this.namaToko = namaToko;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getBiayaPengiriman() {
        return biayaPengiriman;
    }

    public void setBiayaPengiriman(String biayaPengiriman) {
        this.biayaPengiriman = biayaPengiriman;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getKabupaten() {
        return kabupaten;
    }

    public void setKabupaten(String kabupaten) {
        this.kabupaten = kabupaten;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTipeAkun() {
        return tipeAkun;
    }

    public void setTipeAkun(String tipeAkun) {
        this.tipeAkun = tipeAkun;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getBukaToko() {
        return bukaToko;
    }

    public void setBukaToko(String bukaToko) {
        this.bukaToko = bukaToko;
    }

    public String getGambarProfil() {
        return gambarProfil;
    }

    public void setGambarProfil(String gambarProfil) {
        this.gambarProfil = gambarProfil;
    }
}
