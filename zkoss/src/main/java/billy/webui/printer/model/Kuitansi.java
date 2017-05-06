package billy.webui.printer.model;

import java.util.ArrayList;
import java.util.List;

public class Kuitansi {

  private String nomorKuitansi;
  private String mandiri;
  private String namaSales1;
  private String namaSales2;
  private String alamatKantor;
  private String alamatKantor2;
  private String namaKolektor;
  private String kodeKolektor;
  private String namaPelanggan;
  private String alamat;
  private String alamat2;
  private String alamat3;
  private String alamat4;
  private String telepon;
  private String jumlahInWord;
  private String jumlah;
  private String angsuranKe;
  private String tglAngsuran;
  private String namaSupervisor;
  private String sisaPembayaran;


  private List<ItemFaktur> listItemFaktur = new ArrayList<ItemFaktur>();

  public Kuitansi() {

  }

  public String getAlamat() {
    return alamat;
  }

  public String getAlamat2() {
    return alamat2;
  }

  public String getAlamat3() {
    return alamat3;
  }

  public String getAlamat4() {
    return alamat4;
  }

  public String getAlamatKantor() {
    return alamatKantor;
  }

  public String getAlamatKantor2() {
    return alamatKantor2;
  }

  public String getAngsuranKe() {
    return angsuranKe;
  }

  public String getJumlah() {
    return jumlah;
  }

  public String getJumlahInWord() {
    return jumlahInWord;
  }

  public String getKodeKolektor() {
    return kodeKolektor;
  }

  public List<ItemFaktur> getListItemFaktur() {
    return listItemFaktur;
  }

  public String getMandiri() {
    return mandiri;
  }

  public String getNamaKolektor() {
    return namaKolektor;
  }

  public String getNamaPelanggan() {
    return namaPelanggan;
  }

  public String getNamaSales1() {
    return namaSales1;
  }

  public String getNamaSales2() {
    return namaSales2;
  }

  public String getNamaSupervisor() {
    return namaSupervisor;
  }

  public String getNomorKuitansi() {
    return nomorKuitansi;
  }

  public String getSisaPembayaran() {
    return sisaPembayaran;
  }

  public String getTelepon() {
    return telepon;
  }

  public String getTglAngsuran() {
    return tglAngsuran;
  }

  public void setAlamat(String alamat) {
    this.alamat = alamat;
  }

  public void setAlamat2(String alamat2) {
    this.alamat2 = alamat2;
  }

  public void setAlamat3(String alamat3) {
    this.alamat3 = alamat3;
  }

  public void setAlamat4(String alamat4) {
    this.alamat4 = alamat4;
  }

  public void setAlamatKantor(String alamatKantor) {
    this.alamatKantor = alamatKantor;
  }

  public void setAlamatKantor2(String alamatKantor2) {
    this.alamatKantor2 = alamatKantor2;
  }

  public void setAngsuranKe(String angsuranKe) {
    this.angsuranKe = angsuranKe;
  }

  public void setJumlah(String jumlah) {
    this.jumlah = jumlah;
  }

  public void setJumlahInWord(String jumlahInWord) {
    this.jumlahInWord = jumlahInWord;
  }

  public void setKodeKolektor(String kodeKolektor) {
    this.kodeKolektor = kodeKolektor;
  }

  public void setListItemFaktur(List<ItemFaktur> listItemFaktur) {
    this.listItemFaktur = listItemFaktur;
  }

  public void setMandiri(String mandiri) {
    this.mandiri = mandiri;
  }

  public void setNamaKolektor(String namaKolektor) {
    this.namaKolektor = namaKolektor;
  }

  public void setNamaPelanggan(String namaPelanggan) {
    this.namaPelanggan = namaPelanggan;
  }

  public void setNamaSales1(String namaSales1) {
    this.namaSales1 = namaSales1;
  }

  public void setNamaSales2(String namaSales2) {
    this.namaSales2 = namaSales2;
  }

  public void setNamaSupervisor(String namaSupervisor) {
    this.namaSupervisor = namaSupervisor;
  }

  public void setNomorKuitansi(String nomorKuitansi) {
    this.nomorKuitansi = nomorKuitansi;
  }

  public void setSisaPembayaran(String sisaPembayaran) {
    this.sisaPembayaran = sisaPembayaran;
  }

  public void setTelepon(String telepon) {
    this.telepon = telepon;
  }

  public void setTglAngsuran(String tglAngsuran) {
    this.tglAngsuran = tglAngsuran;
  }

  public void tambahItemFaktur(ItemFaktur itemFaktur) {
    this.listItemFaktur.add(itemFaktur);
  }

}
