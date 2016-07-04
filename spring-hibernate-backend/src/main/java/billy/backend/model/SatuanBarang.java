package billy.backend.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class SatuanBarang implements java.io.Serializable, Entity {

  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private String kodeSatuanBarang;
  private String deskripsiSatuanBarang;
  private boolean satuanStandarBarang;
  private long nilaiStandarSatuan;
  private long nilaiKonversi;
  private Date lastUpdate;
  private String updatedBy;


  public SatuanBarang() {}

  public SatuanBarang(long id, String kodeSatuanBarang) {
    this.setId(id);
    this.kodeSatuanBarang = kodeSatuanBarang;
  }

  public SatuanBarang(long id, String kodeSatuanBarang, String deskripsiSatuanBarang,
      boolean satuanStandarBarang) {
    this.setId(id);
    this.kodeSatuanBarang = kodeSatuanBarang;
    this.deskripsiSatuanBarang = deskripsiSatuanBarang;
    this.satuanStandarBarang = satuanStandarBarang;

  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof SatuanBarang) {
      SatuanBarang satuanBarang = (SatuanBarang) obj;
      return equals(satuanBarang);
    }

    return false;
  }

  public boolean equals(SatuanBarang satuanBarang) {
    return getId() == satuanBarang.getId();
  }

  public String getDeskripsiSatuanBarang() {
    return deskripsiSatuanBarang;
  }

  @Override
  public long getId() {
    return id;
  }

  public String getKodeSatuanBarang() {
    return kodeSatuanBarang;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public long getNilaiKonversi() {
    return nilaiKonversi;
  }


  public long getNilaiStandarSatuan() {
    return nilaiStandarSatuan;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public int getVersion() {
    return this.version;
  }

  @Override
  public int hashCode() {
    return Long.valueOf(getId()).hashCode();
  }

  @Override
  public boolean isNew() {
    return (getId() == Long.MIN_VALUE + 1);
  }

  public boolean isSatuanStandarBarang() {
    return satuanStandarBarang;
  }

  public void setDeskripsiSatuanBarang(String deskripsiSatuanBarang) {
    this.deskripsiSatuanBarang = deskripsiSatuanBarang;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public void setKodeSatuanBarang(String kodeSatuanBarang) {
    this.kodeSatuanBarang = kodeSatuanBarang;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public void setNilaiKonversi(long nilaiKonversi) {
    this.nilaiKonversi = nilaiKonversi;
  }

  public void setNilaiStandarSatuan(long nilaiStandarSatuan) {
    this.nilaiStandarSatuan = nilaiStandarSatuan;
  }

  public void setSatuanStandarBarang(boolean satuanStandarBarang) {
    this.satuanStandarBarang = satuanStandarBarang;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", getId()).toString();
  }

}
