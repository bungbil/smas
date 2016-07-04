package billy.webui.report.komisi.model;

import java.math.BigDecimal;

public class KomisiSales {


  private String namaBarang;
  private Double qty;
  private int intervalKredit;
  private BigDecimal komisiSales;
  private BigDecimal tabunganSales;
  private BigDecimal jumlah;

  public KomisiSales() {

  }

  public KomisiSales(String namaBarang, Double qty, int intervalKredit, BigDecimal komisiSales,
      BigDecimal tabunganSales, BigDecimal jumlah) {
    super();
    this.namaBarang = namaBarang;
    this.qty = qty;
    this.intervalKredit = intervalKredit;
    this.komisiSales = komisiSales;
    this.tabunganSales = tabunganSales;
    this.jumlah = jumlah;
  }

  public int getIntervalKredit() {
    return intervalKredit;
  }

  public BigDecimal getJumlah() {
    return jumlah;
  }

  public BigDecimal getKomisiSales() {
    return komisiSales;
  }

  public String getNamaBarang() {
    return namaBarang;
  }

  public Double getQty() {
    return qty;
  }

  public BigDecimal getTabunganSales() {
    return tabunganSales;
  }

  public void setIntervalKredit(int intervalKredit) {
    this.intervalKredit = intervalKredit;
  }

  public void setJumlah(BigDecimal jumlah) {
    this.jumlah = jumlah;
  }

  public void setKomisiSales(BigDecimal komisiSales) {
    this.komisiSales = komisiSales;
  }

  public void setNamaBarang(String namaBarang) {
    this.namaBarang = namaBarang;
  }

  public void setQty(Double qty) {
    this.qty = qty;
  }

  public void setTabunganSales(BigDecimal tabunganSales) {
    this.tabunganSales = tabunganSales;
  }

  @Override
  public String toString() {
    return String
        .format(
            "KomisiSales [namaBarang=%s, qty=%s, intervalKredit=%s, komisiSales=%s, tabunganSales=%s, jumlah=%s]",
            namaBarang, qty, intervalKredit, komisiSales, tabunganSales, jumlah);
  }


}
