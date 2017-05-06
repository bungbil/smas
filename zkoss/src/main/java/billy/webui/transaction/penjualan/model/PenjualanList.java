package billy.webui.transaction.penjualan.model;

import java.math.BigDecimal;

public class PenjualanList {

  private String noFaktur;
  private String namaPelanggan;
  private String namaBarang;
  private BigDecimal hargaBarang = BigDecimal.ZERO;
  private BigDecimal downPayment = BigDecimal.ZERO;
  private String sales1;
  private String sales2;
  private String divisi;
  private String mandiri;


  public PenjualanList() {

  }


  public String getDivisi() {
    return divisi;
  }


  public BigDecimal getDownPayment() {
    return downPayment;
  }


  public BigDecimal getHargaBarang() {
    return hargaBarang;
  }


  public String getMandiri() {
    return mandiri;
  }


  public String getNamaBarang() {
    return namaBarang;
  }


  public String getNamaPelanggan() {
    return namaPelanggan;
  }


  public String getNoFaktur() {
    return noFaktur;
  }


  public String getSales1() {
    return sales1;
  }


  public String getSales2() {
    return sales2;
  }


  public void setDivisi(String divisi) {
    this.divisi = divisi;
  }


  public void setDownPayment(BigDecimal downPayment) {
    this.downPayment = downPayment;
  }


  public void setHargaBarang(BigDecimal hargaBarang) {
    this.hargaBarang = hargaBarang;
  }


  public void setMandiri(String mandiri) {
    this.mandiri = mandiri;
  }


  public void setNamaBarang(String namaBarang) {
    this.namaBarang = namaBarang;
  }


  public void setNamaPelanggan(String namaPelanggan) {
    this.namaPelanggan = namaPelanggan;
  }


  public void setNoFaktur(String noFaktur) {
    this.noFaktur = noFaktur;
  }


  public void setSales1(String sales1) {
    this.sales1 = sales1;
  }


  public void setSales2(String sales2) {
    this.sales2 = sales2;
  }


  @Override
  public String toString() {
    return String
        .format(
            "PenjualanList [noFaktur=%s, namaPelanggan=%s, namaBarang=%s, hargaBarang=%s, downPayment=%s, sales1=%s, sales2=%s, divisi=%s, mandiri=%s]",
            noFaktur, namaPelanggan, namaBarang, hargaBarang, downPayment, sales1, sales2, divisi,
            mandiri);
  }


}
