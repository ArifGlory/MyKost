package iqbal.mykost.Kelas;

/**
 * Created by Glory on 31/10/2018.
 */

public class Kosan {
    public String namaKos;
    public String alamat;
    public String latlon;
    public String harga;
    public String isAC;
    public String isKmrMandi;
    public String isKasur;
    public String isLemari;
    public String isWifi;
    public String isDekatKampus;
    public String nearKampus;
    public String downloadUrl;
    public String sisaKamar;
    public String uidPemilik;

    public Kosan(String namaKos, String alamat, String latlon, String harga, String isAC,
                 String isKmrMandi, String isKasur, String isLemari, String isDekatKampus,
                 String nearKampus, String downloadUrl,String sisaKamar,String isWifi,String uidPemilik) {
        this.namaKos = namaKos;
        this.alamat = alamat;
        this.latlon = latlon;
        this.harga = harga;
        this.isAC = isAC;
        this.isKmrMandi = isKmrMandi;
        this.isKasur = isKasur;
        this.isLemari = isLemari;
        this.isDekatKampus = isDekatKampus;
        this.nearKampus = nearKampus;
        this.downloadUrl = downloadUrl;
        this.sisaKamar = sisaKamar;
        this.isWifi = isWifi;
        this.uidPemilik = uidPemilik;
    }

    public String getNamaKos() {
        return namaKos;
    }

    public void setNamaKos(String namaKos) {
        this.namaKos = namaKos;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getLatlon() {
        return latlon;
    }

    public void setLatlon(String latlon) {
        this.latlon = latlon;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getIsAC() {
        return isAC;
    }

    public void setIsAC(String isAC) {
        this.isAC = isAC;
    }

    public String getIsKmrMandi() {
        return isKmrMandi;
    }

    public void setIsKmrMandi(String isKmrMandi) {
        this.isKmrMandi = isKmrMandi;
    }

    public String getIsKasur() {
        return isKasur;
    }

    public void setIsKasur(String isKasur) {
        this.isKasur = isKasur;
    }

    public String getIsLemari() {
        return isLemari;
    }

    public void setIsLemari(String isLemari) {
        this.isLemari = isLemari;
    }

    public String getIsWifi() {
        return isWifi;
    }

    public void setIsWifi(String isWifi) {
        this.isWifi = isWifi;
    }

    public String getIsDekatKampus() {
        return isDekatKampus;
    }

    public void setIsDekatKampus(String isDekatKampus) {
        this.isDekatKampus = isDekatKampus;
    }

    public String getNearKampus() {
        return nearKampus;
    }

    public void setNearKampus(String nearKampus) {
        this.nearKampus = nearKampus;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getSisaKamar() {
        return sisaKamar;
    }

    public void setSisaKamar(String sisaKamar) {
        this.sisaKamar = sisaKamar;
    }

    public String getUidPemilik() {
        return uidPemilik;
    }

    public void setUidPemilik(String uidPemilik) {
        this.uidPemilik = uidPemilik;
    }
}
