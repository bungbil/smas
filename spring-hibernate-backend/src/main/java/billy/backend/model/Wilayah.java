package billy.backend.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Wilayah implements java.io.Serializable, Entity {

  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private String kodeWilayah;
  private String namaWilayah;
  private String status;
  private Date lastUpdate;
  private String updatedBy;


  public Wilayah() {}

  public Wilayah(long id, String kodeWilayah) {
    this.setId(id);
    this.kodeWilayah = kodeWilayah;
  }

  public Wilayah(long id, String kodeWilayah, String namaWilayah, String status) {
    this.setId(id);
    this.kodeWilayah = kodeWilayah;
    this.namaWilayah = namaWilayah;
    this.status = status;

  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof Wilayah) {
      Wilayah wilayah = (Wilayah) obj;
      return equals(wilayah);
    }

    return false;
  }

  public boolean equals(Wilayah wilayah) {
    return getId() == wilayah.getId();
  }

  @Override
  public long getId() {
    return id;
  }

  public String getKodeWilayah() {
    return kodeWilayah;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public String getNamaWilayah() {
    return namaWilayah;
  }

  public String getStatus() {
    return status;
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

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public void setKodeWilayah(String kodeWilayah) {
    this.kodeWilayah = kodeWilayah;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public void setNamaWilayah(String namaWilayah) {
    this.namaWilayah = namaWilayah;
  }

  public void setStatus(String status) {
    this.status = status;
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
