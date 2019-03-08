package billy.webui.report.piutangsales.model;

import java.math.BigDecimal;

public class PiutangSales {

  private String nomorFaktur;
  private String namaPelanggan;
  private String kodePartner;
  private String intervalKredit;
  private String namaBarang;
  private Double qtyKirim;
  private BigDecimal penjualanBarang;
  private BigDecimal penerimaanPenjualan;
  private BigDecimal sisaPiutang;


  public PiutangSales() {

  }


  public PiutangSales(String nomorFaktur, String namaPelanggan, String kodePartner,
      String intervalKredit, String namaBarang, Double qtyKirim, BigDecimal penjualanBarang,
      BigDecimal penerimaanPenjualan, BigDecimal komisiPenjualan) {
    super();
    this.nomorFaktur = nomorFaktur;
    this.namaPelanggan = namaPelanggan;
    this.kodePartner = kodePartner;
    this.intervalKredit = intervalKredit;
    this.namaBarang = namaBarang;
    this.qtyKirim = qtyKirim;
    this.penjualanBarang = penjualanBarang;
    this.penerimaanPenjualan = penerimaanPenjualan;
    this.sisaPiutang = sisaPiutang;
  }


  public String getIntervalKredit() {
    return intervalKredit;
  }


  public String getKodePartner() {
    return kodePartner;
  }


  public String getNamaBarang() {
    return namaBarang;
  }


  public String getNamaPelanggan() {
    return namaPelanggan;
  }


  public String getNomorFaktur() {
    return nomorFaktur;
  }


  public BigDecimal getPenerimaanPenjualan() {
    return penerimaanPenjualan;
  }


  public BigDecimal getPenjualanBarang() {
    return penjualanBarang;
  }


  public Double getQtyKirim() {
    return qtyKirim;
  }


  public BigDecimal getSisaPiutang() {
    return sisaPiutang;
  }


  public void setIntervalKredit(String intervalKredit) {
    this.intervalKredit = intervalKredit;
  }


  public void setKodePartner(String kodePartner) {
    this.kodePartner = kodePartner;
  }


  public void setNamaBarang(String namaBarang) {
    this.namaBarang = namaBarang;
  }


  public void setNamaPelanggan(String namaPelanggan) {
    this.namaPelanggan = namaPelanggan;
  }


  public void setNomorFaktur(String nomorFaktur) {
    this.nomorFaktur = nomorFaktur;
  }


  public void setPenerimaanPenjualan(BigDecimal penerimaanPenjualan) {
    this.penerimaanPenjualan = penerimaanPenjualan;
  }


  public void setPenjualanBarang(BigDecimal penjualanBarang) {
    this.penjualanBarang = penjualanBarang;
  }


  public void setQtyKirim(Double qtyKirim) {
    this.qtyKirim = qtyKirim;
  }


  public void setSisaPiutang(BigDecimal sisaPiutang) {
    this.sisaPiutang = sisaPiutang;
  }


}
