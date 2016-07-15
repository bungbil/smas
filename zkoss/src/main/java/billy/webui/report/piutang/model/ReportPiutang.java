package billy.webui.report.piutang.model;

import java.math.BigDecimal;
import java.util.Date;

public class ReportPiutang {
  private String no;
  private String noFaktur;
  private String namaCustomer;
  private BigDecimal nilaiTagih;
  private BigDecimal nilaiPembayaran;
  private Date tglBayar;
  private Date tglKuitansi;
  private String keterangan;


  public ReportPiutang() {

  }


  public ReportPiutang(String no, String noFaktur, String namaCustomer, BigDecimal nilaiTagih,
      BigDecimal nilaiPembayaran, Date tglBayar, Date tglKuitansi, String keterangan) {
    super();
    this.no = no;
    this.noFaktur = noFaktur;
    this.namaCustomer = namaCustomer;
    this.nilaiTagih = nilaiTagih;
    this.nilaiPembayaran = nilaiPembayaran;
    this.tglBayar = tglBayar;
    this.tglKuitansi = tglKuitansi;
    this.keterangan = keterangan;
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


  public Date getTglBayar() {
    return tglBayar;
  }


  public Date getTglKuitansi() {
    return tglKuitansi;
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
            "ReportPiutang [no=%s, noFaktur=%s, namaCustomer=%s, nilaiTagih=%s, nilaiPembayaran=%s, tglBayar=%s, tglKuitansi=%s, keterangan=%s]",
            no, noFaktur, namaCustomer, nilaiTagih, nilaiPembayaran, tglBayar, tglKuitansi,
            keterangan);
  }


}
