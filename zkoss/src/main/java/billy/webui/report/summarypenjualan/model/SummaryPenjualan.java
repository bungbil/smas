package billy.webui.report.summarypenjualan.model;

import java.math.BigDecimal;

public class SummaryPenjualan {

  private String namaDivisi;
  private String namaBarang;
  private int unitSetTerjual;
  private BigDecimal penjualanBarang;
  private BigDecimal penerimaanPenjualan;
  private BigDecimal sisaPiutang;
  private boolean bonus;


  public SummaryPenjualan() {

  }


  public SummaryPenjualan(String namaDivisi, String namaBarang, int unitSetTerjual,
      BigDecimal penjualanBarang, BigDecimal penerimaanPenjualan, BigDecimal sisaPiutang) {
    super();
    this.namaDivisi = namaDivisi;
    this.namaBarang = namaBarang;
    this.unitSetTerjual = unitSetTerjual;
    this.penjualanBarang = penjualanBarang;
    this.penerimaanPenjualan = penerimaanPenjualan;
    this.sisaPiutang = sisaPiutang;
  }


  public String getNamaBarang() {
    return namaBarang;
  }


  public String getNamaDivisi() {
    return namaDivisi;
  }


  public BigDecimal getPenerimaanPenjualan() {
    return penerimaanPenjualan;
  }


  public BigDecimal getPenjualanBarang() {
    return penjualanBarang;
  }


  public BigDecimal getSisaPiutang() {
    return sisaPiutang;
  }


  public int getUnitSetTerjual() {
    return unitSetTerjual;
  }


  public boolean isBonus() {
    return bonus;
  }


  public void setBonus(boolean bonus) {
    this.bonus = bonus;
  }


  public void setNamaBarang(String namaBarang) {
    this.namaBarang = namaBarang;
  }


  public void setNamaDivisi(String namaDivisi) {
    this.namaDivisi = namaDivisi;
  }


  public void setPenerimaanPenjualan(BigDecimal penerimaanPenjualan) {
    this.penerimaanPenjualan = penerimaanPenjualan;
  }


  public void setPenjualanBarang(BigDecimal penjualanBarang) {
    this.penjualanBarang = penjualanBarang;
  }


  public void setSisaPiutang(BigDecimal sisaPiutang) {
    this.sisaPiutang = sisaPiutang;
  }


  public void setUnitSetTerjual(int unitSetTerjual) {
    this.unitSetTerjual = unitSetTerjual;
  }


}
