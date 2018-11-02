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
}
