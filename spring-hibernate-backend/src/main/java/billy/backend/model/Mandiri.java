package billy.backend.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Mandiri implements java.io.Serializable, Entity {

  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private String kodeMandiri;
  private String deskripsiMandiri;
  private Date lastUpdate;
  private String updatedBy;


  public Mandiri() {}

  public Mandiri(long id, String kodeMandiri) {
    this.setId(id);
    this.kodeMandiri = kodeMandiri;
  }

  public Mandiri(long id, String kodeMandiri, String deskripsiMandiri) {
    this.setId(id);
    this.kodeMandiri = kodeMandiri;
    this.deskripsiMandiri = deskripsiMandiri;
  }

  public boolean equals(Mandiri mandiri) {
    return getId() == mandiri.getId();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof Mandiri) {
      Mandiri mandiri = (Mandiri) obj;
      return equals(mandiri);
    }

    return false;
  }

  public String getDeskripsiMandiri() {
    return deskripsiMandiri;
  }

  @Override
  public long getId() {
    return id;
  }

  public String getKodeMandiri() {
    return kodeMandiri;
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

  public void setDeskripsiMandiri(String deskripsiMandiri) {
    this.deskripsiMandiri = deskripsiMandiri;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public void setKodeMandiri(String kodeMandiri) {
    this.kodeMandiri = kodeMandiri;
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
