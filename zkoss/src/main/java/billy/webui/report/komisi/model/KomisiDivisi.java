package billy.webui.report.komisi.model;

import java.math.BigDecimal;

public class KomisiDivisi {


  private String namaBarang;
  private int qty;
  private BigDecimal oprDivisi;
  private BigDecimal orDivisi;
  private BigDecimal jumlah;


  public KomisiDivisi() {

  }


  public KomisiDivisi(String namaBarang, int qty, BigDecimal oprDivisi, BigDecimal orDivisi,
      BigDecimal jumlah) {
    super();
    this.namaBarang = namaBarang;
    this.qty = qty;
    this.oprDivisi = oprDivisi;
    this.orDivisi = orDivisi;
    this.jumlah = jumlah;
  }


  public BigDecimal getJumlah() {
    return jumlah;
  }


  public String getNamaBarang() {
    return namaBarang;
  }


  public BigDecimal getOprDivisi() {
    return oprDivisi;
  }


  public BigDecimal getOrDivisi() {
    return orDivisi;
  }


  public int getQty() {
    return qty;
  }


  public void setJumlah(BigDecimal jumlah) {
    this.jumlah = jumlah;
  }


  public void setNamaBarang(String namaBarang) {
    this.namaBarang = namaBarang;
  }


  public void setOprDivisi(BigDecimal oprDivisi) {
    this.oprDivisi = oprDivisi;
  }


  public void setOrDivisi(BigDecimal orDivisi) {
    this.orDivisi = orDivisi;
  }


  public void setQty(int qty) {
    this.qty = qty;
  }


  @Override
  public String toString() {
    return String.format(
        "KomisiDivisi [namaBarang=%s, qty=%s, oprDivisi=%s, orDivisi=%s, jumlah=%s]", namaBarang,
        qty, oprDivisi, orDivisi, jumlah);
  }


}
