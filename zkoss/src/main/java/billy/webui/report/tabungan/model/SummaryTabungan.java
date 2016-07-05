package billy.webui.report.tabungan.model;

import java.math.BigDecimal;

public class SummaryTabungan {

  private String namaDivisi;
  private String namaSales;
  private BigDecimal tabungan1 = BigDecimal.ZERO;
  private BigDecimal tabungan2 = BigDecimal.ZERO;
  private BigDecimal tabungan3 = BigDecimal.ZERO;
  private BigDecimal tabungan4 = BigDecimal.ZERO;
  private BigDecimal tabungan5 = BigDecimal.ZERO;
  private BigDecimal tabungan6 = BigDecimal.ZERO;
  private BigDecimal tabungan7 = BigDecimal.ZERO;
  private BigDecimal tabungan8 = BigDecimal.ZERO;
  private BigDecimal tabungan9 = BigDecimal.ZERO;
  private BigDecimal tabungan10 = BigDecimal.ZERO;
  private BigDecimal tabungan11 = BigDecimal.ZERO;
  private BigDecimal tabungan12 = BigDecimal.ZERO;
  private BigDecimal total = BigDecimal.ZERO;


  public SummaryTabungan() {

  }


  public String getNamaDivisi() {
    return namaDivisi;
  }


  public String getNamaSales() {
    return namaSales;
  }


  public BigDecimal getTabungan1() {
    return tabungan1;
  }


  public BigDecimal getTabungan10() {
    return tabungan10;
  }


  public BigDecimal getTabungan11() {
    return tabungan11;
  }


  public BigDecimal getTabungan12() {
    return tabungan12;
  }


  public BigDecimal getTabungan2() {
    return tabungan2;
  }


  public BigDecimal getTabungan3() {
    return tabungan3;
  }


  public BigDecimal getTabungan4() {
    return tabungan4;
  }


  public BigDecimal getTabungan5() {
    return tabungan5;
  }


  public BigDecimal getTabungan6() {
    return tabungan6;
  }


  public BigDecimal getTabungan7() {
    return tabungan7;
  }


  public BigDecimal getTabungan8() {
    return tabungan8;
  }


  public BigDecimal getTabungan9() {
    return tabungan9;
  }


  public BigDecimal getTotal() {
    return total;
  }


  public void setNamaDivisi(String namaDivisi) {
    this.namaDivisi = namaDivisi;
  }


  public void setNamaSales(String namaSales) {
    this.namaSales = namaSales;
  }


  public void setTabungan1(BigDecimal tabungan1) {
    this.tabungan1 = tabungan1;
  }


  public void setTabungan10(BigDecimal tabungan10) {
    this.tabungan10 = tabungan10;
  }


  public void setTabungan11(BigDecimal tabungan11) {
    this.tabungan11 = tabungan11;
  }


  public void setTabungan12(BigDecimal tabungan12) {
    this.tabungan12 = tabungan12;
  }


  public void setTabungan2(BigDecimal tabungan2) {
    this.tabungan2 = tabungan2;
  }


  public void setTabungan3(BigDecimal tabungan3) {
    this.tabungan3 = tabungan3;
  }


  public void setTabungan4(BigDecimal tabungan4) {
    this.tabungan4 = tabungan4;
  }


  public void setTabungan5(BigDecimal tabungan5) {
    this.tabungan5 = tabungan5;
  }


  public void setTabungan6(BigDecimal tabungan6) {
    this.tabungan6 = tabungan6;
  }


  public void setTabungan7(BigDecimal tabungan7) {
    this.tabungan7 = tabungan7;
  }


  public void setTabungan8(BigDecimal tabungan8) {
    this.tabungan8 = tabungan8;
  }


  public void setTabungan9(BigDecimal tabungan9) {
    this.tabungan9 = tabungan9;
  }


  public void setTotal(BigDecimal total) {
    this.total = total;
  }


  @Override
  public String toString() {
    return String
        .format(
            "SummaryTabungan [namaDivisi=%s, namaSales=%s, tabungan1=%s, tabungan2=%s, tabungan3=%s, tabungan4=%s, tabungan5=%s, tabungan6=%s, tabungan7=%s, tabungan8=%s, tabungan9=%s, tabungan10=%s, tabungan11=%s, tabungan12=%s, total=%s]",
            namaDivisi, namaSales, tabungan1, tabungan2, tabungan3, tabungan4, tabungan5,
            tabungan6, tabungan7, tabungan8, tabungan9, tabungan10, tabungan11, tabungan12, total);
  }


}
