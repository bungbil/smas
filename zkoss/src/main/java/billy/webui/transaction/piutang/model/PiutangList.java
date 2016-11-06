package billy.webui.transaction.piutang.model;

import java.math.BigDecimal;
import java.util.Date;

public class PiutangList {

  private String noFaktur;
  private String noKwitansi;
  private Integer pembayaranKe;
  private Date tglJatuhTempo;
  private BigDecimal nilaiTagihan = BigDecimal.ZERO;
  private String namaKolektor;


  public PiutangList() {

  }


  public String getNamaKolektor() {
    return namaKolektor;
  }


  public BigDecimal getNilaiTagihan() {
    return nilaiTagihan;
  }


  public String getNoFaktur() {
    return noFaktur;
  }


  public String getNoKwitansi() {
    return noKwitansi;
  }


  public Integer getPembayaranKe() {
    return pembayaranKe;
  }


  public Date getTglJatuhTempo() {
    return tglJatuhTempo;
  }


  public void setNamaKolektor(String namaKolektor) {
    this.namaKolektor = namaKolektor;
  }


  public void setNilaiTagihan(BigDecimal nilaiTagihan) {
    this.nilaiTagihan = nilaiTagihan;
  }


  public void setNoFaktur(String noFaktur) {
    this.noFaktur = noFaktur;
  }


  public void setNoKwitansi(String noKwitansi) {
    this.noKwitansi = noKwitansi;
  }


  public void setPembayaranKe(Integer pembayaranKe) {
    this.pembayaranKe = pembayaranKe;
  }


  public void setTglJatuhTempo(Date tglJatuhTempo) {
    this.tglJatuhTempo = tglJatuhTempo;
  }


  @Override
  public String toString() {
    return String
        .format(
            "PiutangList [noFaktur=%s, noKwitansi=%s, pembayaranKe=%s, tglJatuhTempo=%s, nilaiTagihan=%s, namaKolektor=%s]",
            noFaktur, noKwitansi, pembayaranKe, tglJatuhTempo, nilaiTagihan, namaKolektor);
  }


}
