package billy.backend.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class KategoriBarang implements java.io.Serializable, Entity {

  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private String kodeKategoriBarang;
  private String deskripsiKategoriBarang;
  private Date lastUpdate;
  private String updatedBy;


  public KategoriBarang() {}

  public KategoriBarang(long id, String kodeKategoriBarang) {
    this.setId(id);
    this.kodeKategoriBarang = kodeKategoriBarang;
  }

  public KategoriBarang(long id, String kodeKategoriBarang, String deskripsiKategoriBarang) {
    this.setId(id);
    this.kodeKategoriBarang = kodeKategoriBarang;
    this.deskripsiKategoriBarang = deskripsiKategoriBarang;
  }

  public boolean equals(KategoriBarang kategoriBarang) {
    return getId() == kategoriBarang.getId();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof KategoriBarang) {
      KategoriBarang kategoriBarang = (KategoriBarang) obj;
      return equals(kategoriBarang);
    }

    return false;
  }

  public String getDeskripsiKategoriBarang() {
    return deskripsiKategoriBarang;
  }

  @Override
  public long getId() {
    return id;
  }

  public String getKodeKategoriBarang() {
    return kodeKategoriBarang;
  }

  public Date getLastUpdate() {
    return lastUpdate;
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

  public void setDeskripsiKategoriBarang(String deskripsiKategoriBarang) {
    this.deskripsiKategoriBarang = deskripsiKategoriBarang;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public void setKodeKategoriBarang(String kodeKategoriBarang) {
    this.kodeKategoriBarang = kodeKategoriBarang;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
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
