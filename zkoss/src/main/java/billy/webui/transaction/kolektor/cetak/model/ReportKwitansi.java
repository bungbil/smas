package billy.webui.transaction.kolektor.cetak.model;

import java.math.BigDecimal;
import java.util.Date;

public class ReportKwitansi {
  private String no;
  private String noFaktur;
  private String namaCustomer;
  private BigDecimal nilaiTagih;
  private BigDecimal nilaiPembayaran;
  private BigDecimal diskon;
  private Date tglBayar;
  private Date tglBawa;
  private Date tglKuitansi;
  private String keterangan;


  public ReportKwitansi() {

  }


  public BigDecimal getDiskon() {
    return diskon;
  }


  public String getKeterangan() {
    return keterangan;
  }


  public String getNamaCustomer() {
    return namaCustomer;
  }


  public BigDecimal getNilaiPembayaran() {
    return nilaiPembayaran;
  }


  public BigDecimal getNilaiTagih() {
    return nilaiTagih;
  }


  public String getNo() {
    return no;
  }


  public String getNoFaktur() {
    return noFaktur;
  }


  public Date getTglBawa() {
    return tglBawa;
  }


  public Date getTglBayar() {
    return tglBayar;
  }


  public Date getTglKuitansi() {
    return tglKuitansi;
  }


  public void setDiskon(BigDecimal diskon) {
    this.diskon = diskon;
  }


  public void setKeterangan(String keterangan) {
    this.keterangan = keterangan;
  }


  public void setNamaCustomer(String namaCustomer) {
    this.namaCustomer = namaCustomer;
  }


  public void setNilaiPembayaran(BigDecimal nilaiPembayaran) {
    this.nilaiPembayaran = nilaiPembayaran;
  }


  public void setNilaiTagih(BigDecimal nilaiTagih) {
    this.nilaiTagih = nilaiTagih;
  }


  public void setNo(String no) {
    this.no = no;
  }


  public void setNoFaktur(String noFaktur) {
    this.noFaktur = noFaktur;
  }


  public void setTglBawa(Date tglBawa) {
    this.tglBawa = tglBawa;
  }


  public void setTglBayar(Date tglBayar) {
    this.tglBayar = tglBayar;
  }


  public void setTglKuitansi(Date tglKuitansi) {
    this.tglKuitansi = tglKuitansi;
  }


  @Override
  public String toString() {
    return String
        .format(
            "ReportKwitansi [no=%s, noFaktur=%s, namaCustomer=%s, nilaiTagih=%s, nilaiPembayaran=%s, diskon=%s, tglBayar=%s, tglBawa=%s, tglKuitansi=%s, keterangan=%s]",
            no, noFaktur, namaCustomer, nilaiTagih, nilaiPembayaran, diskon, tglBayar, tglBawa,
            tglKuitansi, keterangan);
  }


}
