package billy.backend.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Status implements java.io.Serializable, Entity {

  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private String deskripsiStatus;
  private Date lastUpdate;
  private String updatedBy;


  public Status() {}

  public Status(long id, String deskripsiStatus) {
    this.setId(id);
    this.deskripsiStatus = deskripsiStatus;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof Status) {
      Status status = (Status) obj;
      return equals(status);
    }

    return false;
  }


  public boolean equals(Status status) {
    return getId() == status.getId();
  }

  public String getDeskripsiStatus() {
    return deskripsiStatus;
  }

  @Override
  public long getId() {
    return id;
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

  public void setDeskripsiStatus(String deskripsiStatus) {
    this.deskripsiStatus = deskripsiStatus;
  }

  @Override
  public void setId(long id) {
    this.id = id;
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
