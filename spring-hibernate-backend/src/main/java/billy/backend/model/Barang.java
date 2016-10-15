package billy.backend.model;

import java.math.BigDecimal;
import java.util.Date;

public class Barang implements java.io.Serializable, Entity {

  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private String kodeBarang;
  private String namaBarang;
  private Wilayah wilayah;
  private BigDecimal divisiOr;
  private BigDecimal divisiOpr;

  private BigDecimal hargaBarang1x;
  private BigDecimal cicilanPerBulan1x;
  private BigDecimal komisiSales1x;
  private BigDecimal tabunganSales1x;
  private BigDecimal hargaBarang2x;
  private BigDecimal cicilanPerBulan2x;
  private BigDecimal komisiSales2x;
  private BigDecimal tabunganSales2x;
  private BigDecimal hargaBarang3x;
  private BigDecimal cicilanPerBulan3x;
  private BigDecimal komisiSales3x;
  private BigDecimal tabunganSales3x;
  private BigDecimal hargaBarang4x;
  private BigDecimal cicilanPerBulan4x;
  private BigDecimal komisiSales4x;
  private BigDecimal tabunganSales4x;
  private BigDecimal hargaBarang5x;
  private BigDecimal cicilanPerBulan5x;
  private BigDecimal komisiSales5x;
  private BigDecimal tabunganSales5x;
  private BigDecimal hargaBarang6x;
  private BigDecimal cicilanPerBulan6x;
  private BigDecimal komisiSales6x;
  private BigDecimal tabunganSales6x;
  private BigDecimal hargaBarang7x;
  private BigDecimal cicilanPerBulan7x;
  private BigDecimal komisiSales7x;
  private BigDecimal tabunganSales7x;
  private BigDecimal hargaBarang8x;
  private BigDecimal cicilanPerBulan8x;
  private BigDecimal komisiSales8x;
  private BigDecimal tabunganSales8x;
  private BigDecimal hargaBarang9x;
  private BigDecimal cicilanPerBulan9x;
  private BigDecimal komisiSales9x;
  private BigDecimal tabunganSales9x;
  private BigDecimal hargaBarang10x;
  private BigDecimal cicilanPerBulan10x;
  private BigDecimal komisiSales10x;
  private BigDecimal tabunganSales10x;

  private Date lastUpdate;
  private String updatedBy;
  private boolean bonus;

  public Barang() {}

  public Barang(long id, int version, String kodeBarang, String namaBarang, Wilayah wilayah,
      BigDecimal divisiOr, BigDecimal divisiOpr, BigDecimal hargaBarang1x,
      BigDecimal cicilanPerBulan1x, BigDecimal komisiSales1x, BigDecimal tabunganSales1x,
      BigDecimal hargaBarang2x, BigDecimal cicilanPerBulan2x, BigDecimal komisiSales2x,
      BigDecimal tabunganSales2x, BigDecimal hargaBarang3x, BigDecimal cicilanPerBulan3x,
      BigDecimal komisiSales3x, BigDecimal tabunganSales3x, BigDecimal hargaBarang4x,
      BigDecimal cicilanPerBulan4x, BigDecimal komisiSales4x, BigDecimal tabunganSales4x,
      BigDecimal hargaBarang5x, BigDecimal cicilanPerBulan5x, BigDecimal komisiSales5x,
      BigDecimal tabunganSales5x, BigDecimal hargaBarang6x, BigDecimal cicilanPerBulan6x,
      BigDecimal komisiSales6x, BigDecimal tabunganSales6x, BigDecimal hargaBarang7x,
      BigDecimal cicilanPerBulan7x, BigDecimal komisiSales7x, BigDecimal tabunganSales7x,
      BigDecimal hargaBarang8x, BigDecimal cicilanPerBulan8x, BigDecimal komisiSales8x,
      BigDecimal tabunganSales8x, BigDecimal hargaBarang9x, BigDecimal cicilanPerBulan9x,
      BigDecimal komisiSales9x, BigDecimal tabunganSales9x, BigDecimal hargaBarang10x,
      BigDecimal cicilanPerBulan10x, BigDecimal komisiSales10x, BigDecimal tabunganSales10x,
      Date lastUpdate, String updatedBy) {

    this.id = id;
    this.version = version;
    this.kodeBarang = kodeBarang;
    this.namaBarang = namaBarang;
    this.wilayah = wilayah;
    this.divisiOr = divisiOr;
    this.divisiOpr = divisiOpr;
    this.hargaBarang1x = hargaBarang1x;
    this.cicilanPerBulan1x = cicilanPerBulan1x;
    this.komisiSales1x = komisiSales1x;
    this.tabunganSales1x = tabunganSales1x;
    this.hargaBarang2x = hargaBarang2x;
    this.cicilanPerBulan2x = cicilanPerBulan2x;
    this.komisiSales2x = komisiSales2x;
    this.tabunganSales2x = tabunganSales2x;
    this.hargaBarang3x = hargaBarang3x;
    this.cicilanPerBulan3x = cicilanPerBulan3x;
    this.komisiSales3x = komisiSales3x;
    this.tabunganSales3x = tabunganSales3x;
    this.hargaBarang4x = hargaBarang4x;
    this.cicilanPerBulan4x = cicilanPerBulan4x;
    this.komisiSales4x = komisiSales4x;
    this.tabunganSales4x = tabunganSales4x;
    this.hargaBarang5x = hargaBarang5x;
    this.cicilanPerBulan5x = cicilanPerBulan5x;
    this.komisiSales5x = komisiSales5x;
    this.tabunganSales5x = tabunganSales5x;
    this.hargaBarang6x = hargaBarang6x;
    this.cicilanPerBulan6x = cicilanPerBulan6x;
    this.komisiSales6x = komisiSales6x;
    this.tabunganSales6x = tabunganSales6x;
    this.hargaBarang7x = hargaBarang7x;
    this.cicilanPerBulan7x = cicilanPerBulan7x;
    this.komisiSales7x = komisiSales7x;
    this.tabunganSales7x = tabunganSales7x;
    this.hargaBarang8x = hargaBarang8x;
    this.cicilanPerBulan8x = cicilanPerBulan8x;
    this.komisiSales8x = komisiSales8x;
    this.tabunganSales8x = tabunganSales8x;
    this.hargaBarang9x = hargaBarang9x;
    this.cicilanPerBulan9x = cicilanPerBulan9x;
    this.komisiSales9x = komisiSales9x;
    this.tabunganSales9x = tabunganSales9x;
    this.hargaBarang10x = hargaBarang10x;
    this.cicilanPerBulan10x = cicilanPerBulan10x;
    this.komisiSales10x = komisiSales10x;
    this.tabunganSales10x = tabunganSales10x;
    this.lastUpdate = lastUpdate;
    this.updatedBy = updatedBy;
  }

  public Barang(long id, String kodeBarang) {
    this.setId(id);
    this.kodeBarang = kodeBarang;
  }

  public boolean equals(Barang barang) {
    return getId() == barang.getId();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof Barang) {
      Barang barang = (Barang) obj;
      return equals(barang);
    }

    return false;
  }


  public BigDecimal getCicilanPerBulan10x() {
    return cicilanPerBulan10x;
  }

  public BigDecimal getCicilanPerBulan1x() {
    return cicilanPerBulan1x;
  }

  public BigDecimal getCicilanPerBulan2x() {
    return cicilanPerBulan2x;
  }

  public BigDecimal getCicilanPerBulan3x() {
    return cicilanPerBulan3x;
  }

  public BigDecimal getCicilanPerBulan4x() {
    return cicilanPerBulan4x;
  }

  public BigDecimal getCicilanPerBulan5x() {
    return cicilanPerBulan5x;
  }

  public BigDecimal getCicilanPerBulan6x() {
    return cicilanPerBulan6x;
  }

  public BigDecimal getCicilanPerBulan7x() {
    return cicilanPerBulan7x;
  }

  public BigDecimal getCicilanPerBulan8x() {
    return cicilanPerBulan8x;
  }


  public BigDecimal getCicilanPerBulan9x() {
    return cicilanPerBulan9x;
  }

  public BigDecimal getDivisiOpr() {
    return divisiOpr;
  }

  public BigDecimal getDivisiOr() {
    return divisiOr;
  }

  public BigDecimal getHargaBarang10x() {
    return hargaBarang10x;
  }

  public BigDecimal getHargaBarang1x() {
    return hargaBarang1x;
  }

  public BigDecimal getHargaBarang2x() {
    return hargaBarang2x;
  }


  public BigDecimal getHargaBarang3x() {
    return hargaBarang3x;
  }

  public BigDecimal getHargaBarang4x() {
    return hargaBarang4x;
  }

  public BigDecimal getHargaBarang5x() {
    return hargaBarang5x;
  }

  public BigDecimal getHargaBarang6x() {
    return hargaBarang6x;
  }

  public BigDecimal getHargaBarang7x() {
    return hargaBarang7x;
  }

  public BigDecimal getHargaBarang8x() {
    return hargaBarang8x;
  }

  public BigDecimal getHargaBarang9x() {
    return hargaBarang9x;
  }

  @Override
  public long getId() {
    return id;
  }

  public String getKodeBarang() {
    return kodeBarang;
  }

  public BigDecimal getKomisiSales10x() {
    return komisiSales10x;
  }

  public BigDecimal getKomisiSales1x() {
    return komisiSales1x;
  }

  public BigDecimal getKomisiSales2x() {
    return komisiSales2x;
  }

  public BigDecimal getKomisiSales3x() {
    return komisiSales3x;
  }

  public BigDecimal getKomisiSales4x() {
    return komisiSales4x;
  }

  public BigDecimal getKomisiSales5x() {
    return komisiSales5x;
  }

  public BigDecimal getKomisiSales6x() {
    return komisiSales6x;
  }

  public BigDecimal getKomisiSales7x() {
    return komisiSales7x;
  }

  public BigDecimal getKomisiSales8x() {
    return komisiSales8x;
  }

  public BigDecimal getKomisiSales9x() {
    return komisiSales9x;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public String getNamaBarang() {
    return namaBarang;
  }

  public BigDecimal getTabunganSales10x() {
    return tabunganSales10x;
  }

  public BigDecimal getTabunganSales1x() {
    return tabunganSales1x;
  }

  public BigDecimal getTabunganSales2x() {
    return tabunganSales2x;
  }

  public BigDecimal getTabunganSales3x() {
    return tabunganSales3x;
  }

  public BigDecimal getTabunganSales4x() {
    return tabunganSales4x;
  }

  public BigDecimal getTabunganSales5x() {
    return tabunganSales5x;
  }

  public BigDecimal getTabunganSales6x() {
    return tabunganSales6x;
  }

  public BigDecimal getTabunganSales7x() {
    return tabunganSales7x;
  }

  public BigDecimal getTabunganSales8x() {
    return tabunganSales8x;
  }

  public BigDecimal getTabunganSales9x() {
    return tabunganSales9x;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public int getVersion() {
    return this.version;
  }

  public Wilayah getWilayah() {
    return wilayah;
  }

  @Override
  public int hashCode() {
    return Long.valueOf(getId()).hashCode();
  }

  public boolean isBonus() {
    return bonus;
  }

  @Override
  public boolean isNew() {
    return (getId() == Long.MIN_VALUE + 1);
  }

  public void setBonus(boolean bonus) {
    this.bonus = bonus;
  }

  public void setCicilanPerBulan10x(BigDecimal cicilanPerBulan10x) {
    this.cicilanPerBulan10x = cicilanPerBulan10x;
  }

  public void setCicilanPerBulan1x(BigDecimal cicilanPerBulan1x) {
    this.cicilanPerBulan1x = cicilanPerBulan1x;
  }

  public void setCicilanPerBulan2x(BigDecimal cicilanPerBulan2x) {
    this.cicilanPerBulan2x = cicilanPerBulan2x;
  }

  public void setCicilanPerBulan3x(BigDecimal cicilanPerBulan3x) {
    this.cicilanPerBulan3x = cicilanPerBulan3x;
  }

  public void setCicilanPerBulan4x(BigDecimal cicilanPerBulan4x) {
    this.cicilanPerBulan4x = cicilanPerBulan4x;
  }

  public void setCicilanPerBulan5x(BigDecimal cicilanPerBulan5x) {
    this.cicilanPerBulan5x = cicilanPerBulan5x;
  }

  public void setCicilanPerBulan6x(BigDecimal cicilanPerBulan6x) {
    this.cicilanPerBulan6x = cicilanPerBulan6x;
  }

  public void setCicilanPerBulan7x(BigDecimal cicilanPerBulan7x) {
    this.cicilanPerBulan7x = cicilanPerBulan7x;
  }

  public void setCicilanPerBulan8x(BigDecimal cicilanPerBulan8x) {
    this.cicilanPerBulan8x = cicilanPerBulan8x;
  }

  public void setCicilanPerBulan9x(BigDecimal cicilanPerBulan9x) {
    this.cicilanPerBulan9x = cicilanPerBulan9x;
  }

  public void setDivisiOpr(BigDecimal divisiOpr) {
    this.divisiOpr = divisiOpr;
  }

  public void setDivisiOr(BigDecimal divisiOr) {
    this.divisiOr = divisiOr;
  }

  public void setHargaBarang10x(BigDecimal hargaBarang10x) {
    this.hargaBarang10x = hargaBarang10x;
  }

  public void setHargaBarang1x(BigDecimal hargaBarang1x) {
    this.hargaBarang1x = hargaBarang1x;
  }

  public void setHargaBarang2x(BigDecimal hargaBarang2x) {
    this.hargaBarang2x = hargaBarang2x;
  }

  public void setHargaBarang3x(BigDecimal hargaBarang3x) {
    this.hargaBarang3x = hargaBarang3x;
  }

  public void setHargaBarang4x(BigDecimal hargaBarang4x) {
    this.hargaBarang4x = hargaBarang4x;
  }

  public void setHargaBarang5x(BigDecimal hargaBarang5x) {
    this.hargaBarang5x = hargaBarang5x;
  }

  public void setHargaBarang6x(BigDecimal hargaBarang6x) {
    this.hargaBarang6x = hargaBarang6x;
  }

  public void setHargaBarang7x(BigDecimal hargaBarang7x) {
    this.hargaBarang7x = hargaBarang7x;
  }

  public void setHargaBarang8x(BigDecimal hargaBarang8x) {
    this.hargaBarang8x = hargaBarang8x;
  }

  public void setHargaBarang9x(BigDecimal hargaBarang9x) {
    this.hargaBarang9x = hargaBarang9x;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public void setKodeBarang(String kodeBarang) {
    this.kodeBarang = kodeBarang;
  }

  public void setKomisiSales10x(BigDecimal komisiSales10x) {
    this.komisiSales10x = komisiSales10x;
  }

  public void setKomisiSales1x(BigDecimal komisiSales1x) {
    this.komisiSales1x = komisiSales1x;
  }

  public void setKomisiSales2x(BigDecimal komisiSales2x) {
    this.komisiSales2x = komisiSales2x;
  }

  public void setKomisiSales3x(BigDecimal komisiSales3x) {
    this.komisiSales3x = komisiSales3x;
  }

  public void setKomisiSales4x(BigDecimal komisiSales4x) {
    this.komisiSales4x = komisiSales4x;
  }

  public void setKomisiSales5x(BigDecimal komisiSales5x) {
    this.komisiSales5x = komisiSales5x;
  }

  public void setKomisiSales6x(BigDecimal komisiSales6x) {
    this.komisiSales6x = komisiSales6x;
  }

  public void setKomisiSales7x(BigDecimal komisiSales7x) {
    this.komisiSales7x = komisiSales7x;
  }

  public void setKomisiSales8x(BigDecimal komisiSales8x) {
    this.komisiSales8x = komisiSales8x;
  }

  public void setKomisiSales9x(BigDecimal komisiSales9x) {
    this.komisiSales9x = komisiSales9x;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public void setNamaBarang(String namaBarang) {
    this.namaBarang = namaBarang;
  }

  public void setTabunganSales10x(BigDecimal tabunganSales10x) {
    this.tabunganSales10x = tabunganSales10x;
  }

  public void setTabunganSales1x(BigDecimal tabunganSales1x) {
    this.tabunganSales1x = tabunganSales1x;
  }

  public void setTabunganSales2x(BigDecimal tabunganSales2x) {
    this.tabunganSales2x = tabunganSales2x;
  }

  public void setTabunganSales3x(BigDecimal tabunganSales3x) {
    this.tabunganSales3x = tabunganSales3x;
  }

  public void setTabunganSales4x(BigDecimal tabunganSales4x) {
    this.tabunganSales4x = tabunganSales4x;
  }

  public void setTabunganSales5x(BigDecimal tabunganSales5x) {
    this.tabunganSales5x = tabunganSales5x;
  }

  public void setTabunganSales6x(BigDecimal tabunganSales6x) {
    this.tabunganSales6x = tabunganSales6x;
  }

  public void setTabunganSales7x(BigDecimal tabunganSales7x) {
    this.tabunganSales7x = tabunganSales7x;
  }

  public void setTabunganSales8x(BigDecimal tabunganSales8x) {
    this.tabunganSales8x = tabunganSales8x;
  }

  public void setTabunganSales9x(BigDecimal tabunganSales9x) {
    this.tabunganSales9x = tabunganSales9x;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public void setWilayah(Wilayah wilayah) {
    this.wilayah = wilayah;
  }

  @Override
  public String toString() {
    return String
        .format(
            "Barang [id=%s, version=%s, kodeBarang=%s, namaBarang=%s, wilayah=%s, divisiOr=%s, divisiOpr=%s, hargaBarang1x=%s, cicilanPerBulan1x=%s, komisiSales1x=%s, tabunganSales1x=%s, hargaBarang2x=%s, cicilanPerBulan2x=%s, komisiSales2x=%s, tabunganSales2x=%s, hargaBarang3x=%s, cicilanPerBulan3x=%s, komisiSales3x=%s, tabunganSales3x=%s, hargaBarang4x=%s, cicilanPerBulan4x=%s, komisiSales4x=%s, tabunganSales4x=%s, hargaBarang5x=%s, cicilanPerBulan5x=%s, komisiSales5x=%s, tabunganSales5x=%s, hargaBarang6x=%s, cicilanPerBulan6x=%s, komisiSales6x=%s, tabunganSales6x=%s, hargaBarang7x=%s, cicilanPerBulan7x=%s, komisiSales7x=%s, tabunganSales7x=%s, hargaBarang8x=%s, cicilanPerBulan8x=%s, komisiSales8x=%s, tabunganSales8x=%s, hargaBarang9x=%s, cicilanPerBulan9x=%s, komisiSales9x=%s, tabunganSales9x=%s, hargaBarang10x=%s, cicilanPerBulan10x=%s, komisiSales10x=%s, tabunganSales10x=%s, lastUpdate=%s, updatedBy=%s, bonus=%s]",
            id, version, kodeBarang, namaBarang, wilayah, divisiOr, divisiOpr, hargaBarang1x,
            cicilanPerBulan1x, komisiSales1x, tabunganSales1x, hargaBarang2x, cicilanPerBulan2x,
            komisiSales2x, tabunganSales2x, hargaBarang3x, cicilanPerBulan3x, komisiSales3x,
            tabunganSales3x, hargaBarang4x, cicilanPerBulan4x, komisiSales4x, tabunganSales4x,
            hargaBarang5x, cicilanPerBulan5x, komisiSales5x, tabunganSales5x, hargaBarang6x,
            cicilanPerBulan6x, komisiSales6x, tabunganSales6x, hargaBarang7x, cicilanPerBulan7x,
            komisiSales7x, tabunganSales7x, hargaBarang8x, cicilanPerBulan8x, komisiSales8x,
            tabunganSales8x, hargaBarang9x, cicilanPerBulan9x, komisiSales9x, tabunganSales9x,
            hargaBarang10x, cicilanPerBulan10x, komisiSales10x, tabunganSales10x, lastUpdate,
            updatedBy, bonus);
  }

}
