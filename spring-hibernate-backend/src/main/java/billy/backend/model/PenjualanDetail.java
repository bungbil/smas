package billy.backend.model;

import java.math.BigDecimal;

public class PenjualanDetail implements java.io.Serializable, Entity {


  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private Penjualan penjualan;
  private Barang barang;
  private int qty;
  private BigDecimal harga;
  private BigDecimal downPayment;
  private BigDecimal total;
  private BigDecimal komisiSales;
  private BigDecimal tabunganSales;
  private BigDecimal oprDivisi;
  private BigDecimal orDivisi;
  private boolean bonus;

  public PenjualanDetail() {}

  public PenjualanDetail(long id, int version, Penjualan penjualan, Barang barang, int qty,
      BigDecimal harga, BigDecimal downPayment, BigDecimal total, BigDecimal komisiSales,
      BigDecimal tabunganSales, BigDecimal oprDivisi, BigDecimal orDivisi) {
    super();
    this.id = id;
    this.version = version;
    this.penjualan = penjualan;
    this.barang = barang;
    this.qty = qty;
    this.harga = harga;
    this.downPayment = downPayment;
    this.total = total;
    this.komisiSales = komisiSales;
    this.tabunganSales = tabunganSales;
    this.oprDivisi = oprDivisi;
    this.orDivisi = orDivisi;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PenjualanDetail other = (PenjualanDetail) obj;
    if (barang == null) {
      if (other.barang != null)
        return false;
    } else if (!barang.equals(other.barang))
      return false;
    if (harga == null) {
      if (other.harga != null)
        return false;
    } else if (!harga.equals(other.harga))
      return false;
    if (id != other.id)
      return false;
    if (penjualan == null) {
      if (other.penjualan != null)
        return false;
    } else if (!penjualan.equals(other.penjualan))
      return false;
    if (qty != other.qty)
      return false;
    if (total == null) {
      if (other.total != null)
        return false;
    } else if (!total.equals(other.total))
      return false;
    if (version != other.version)
      return false;
    return true;
  }

  public boolean equals(PenjualanDetail obj) {
    return getId() == obj.getId();
  }


  public Barang getBarang() {
    return barang;
  }

  public BigDecimal getDownPayment() {
    return downPayment;
  }

  public BigDecimal getHarga() {
    return harga;
  }

  @Override
  public long getId() {
    return id;
  }

  public BigDecimal getKomisiSales() {
    return komisiSales;
  }

  public BigDecimal getOprDivisi() {
    return oprDivisi;
  }

  public BigDecimal getOrDivisi() {
    return orDivisi;
  }

  public Penjualan getPenjualan() {
    return penjualan;
  }

  public int getQty() {
    return qty;
  }

  public BigDecimal getTabunganSales() {
    return tabunganSales;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public int getVersion() {
    return version;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((barang == null) ? 0 : barang.hashCode());
    result = prime * result + ((harga == null) ? 0 : harga.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((penjualan == null) ? 0 : penjualan.hashCode());
    result = prime * result + qty;
    result = prime * result + ((total == null) ? 0 : total.hashCode());
    result = prime * result + version;
    return result;
  }

  public boolean isBonus() {
    return bonus;
  }

  @Override
  public boolean isNew() {
    return (getId() == Long.MIN_VALUE + 1);
  }

  public void setBarang(Barang barang) {
    this.barang = barang;
  }

  public void setBonus(boolean bonus) {
    this.bonus = bonus;
  }

  public void setDownPayment(BigDecimal downPayment) {
    this.downPayment = downPayment;
  }

  public void setHarga(BigDecimal harga) {
    this.harga = harga;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public void setKomisiSales(BigDecimal komisiSales) {
    this.komisiSales = komisiSales;
  }

  public void setOprDivisi(BigDecimal oprDivisi) {
    this.oprDivisi = oprDivisi;
  }

  public void setOrDivisi(BigDecimal orDivisi) {
    this.orDivisi = orDivisi;
  }

  public void setPenjualan(Penjualan penjualan) {
    this.penjualan = penjualan;
  }

  public void setQty(int qty) {
    this.qty = qty;
  }

  public void setTabunganSales(BigDecimal tabunganSales) {
    this.tabunganSales = tabunganSales;
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return String
        .format(
            "PenjualanDetail [id=%s, version=%s, penjualan=%s, barang=%s, qty=%s, harga=%s, downPayment=%s, total=%s, komisiSales=%s, tabunganSales=%s, oprDivisi=%s, orDivisi=%s, bonus=%s]",
            id, version, penjualan, barang, qty, harga, downPayment, total, komisiSales,
            tabunganSales, oprDivisi, orDivisi, bonus);
  }
}
